#include <msp430.h> 
#include "RF430.h"

/*
 * main.c
 */
//Global values
//Heartbeat count values
unsigned char getNewPulse = 0;
unsigned short pulseTimerCounter = 0;
unsigned short pulseCount = 0;
//NFC values
unsigned char into_fired = 0;
unsigned char NDEF_Application_Data[] = RF430_APP_DATA;
unsigned char NDEF_Application_Data2[] = RF430_DEFAULT_DATA;

void setup() {
	WDTCTL = WDTPW + WDTTMSEL + WDTCNTCL + WDTIS0; //watchdog clock source SMCLK; timer+ interval select SMCLK/8192; 1ms  1s = 976~977counts.
	IE1 |= WDTIE; //interrupt enable;

	BCSCTL1 = CALBC1_8MHZ;  //Set range
	DCOCTL = CALDCO_8MHZ;	//SMCLK = MCLK = DCO = 8 MHz	ACLK = LF oscillator

	//Configure pins for P1
	// Configure pins for I2C
	P1SEL = (unsigned char) (0x00 | BIT6 | BIT7); //Selecting I2C pin function
	P1SEL2 = (unsigned char) (0x00 | BIT6 | BIT7);
	P1OUT = (unsigned char) (0x00 | BIT3); //P1.3 pull up and others pull down
	P1DIR = (unsigned char) (0xFF & (~BIT3)); //P1.3 input and others output
	P1REN = (unsigned char) (0xFF & (~BIT0) & (~BIT6) & (~BIT7)); //P1.0 pull up/down resistor disable for LED output, and P1.6 P1.7 disable for I2C.
	//Configure the P1 interrupt: Enable the P1.3 interrupt for signal input
	P1IE = (unsigned char) (0x00);
	P1IFG &= ~(BIT3); //Clear interrupt flag
	P1IES &= ~(BIT3); //Set interrupt trigger as low-to-high transition, since INTO will be setup active low below

	//configure USCI for I2C
	UCB0CTL1 |= UCSWRST;	            	// Software reset enabled
	UCB0CTL0 |= UCMODE_3 + UCMST + UCSYNC; // I2C mode, Master mode, sync
	UCB0CTL1 |= UCSSEL_3 + UCTR;            // SMCLK = 8MHz, transmitter
	UCB0BR0 = 80; 							// Baudrate = SMLK/80 = 100kHz
	UCB0I2CSA = 0x0028;	// slave address - determined by pins E0, E1, and E2 on the RF430CL330H
	UCB0CTL1 &= ~UCSWRST;					// Software reset released

	//Configure pins for P2
	P2SEL = (unsigned char) (0x00);
	P2SEL2 = (unsigned char) (0x00);
	P2OUT = (unsigned char) (0x00 | BIT4);
	P2DIR = (unsigned char) (0xFF & ~(BIT4));
	P2REN = (unsigned char) (0xFF);
	//Configure the P2 interrupt:
	P2IE = (unsigned char) (0x00);
	//P2IFG &= ~(BIT4); //Clear interrupt flag
	//P2IES |= BIT4; //Set interrupt trigger as high-to-low transition, since INTO will be setup active low below

	_enable_interrupt();
	__bis_SR_register(GIE);

	//Initialize
	__delay_cycles(1000);
	P2OUT |= BIT0; //Release the RF430CL330H from Reset
	Write_Register(CONTROL_REG, SW_RESET);
	__delay_cycles(4000000); // Leave time for the RF430CL33H to get itself initialized; should be 20ms or greater
	while (!(Read_Register(STATUS_REG) & READY))
		; // Wait until READY bit has been set
}

int main(void) {
	unsigned short status = 0;
	unsigned char Open_app_signal = 0;
	unsigned char IE_heartbeat = 0;
	setup();

	while (1) {
		Write_Continuous(0, NDEF_Application_Data, 59);
		Write_Register(CONTROL_REG, RF_ENABLE);
		__delay_cycles(4000000);

		status = Read_Register(STATUS_REG);
		pulseTimerCounter = 0;
		while (status & RF_BUSY) {
			if (!IE_heartbeat) {
				P1IE |= BIT3;
				P1IFG &= ~(BIT3);
				IE_heartbeat = 1;
			}
			P1OUT ^= BIT0;
			status = Read_Register(STATUS_REG);
			if (pulseTimerCounter > 5000) pulseTimerCounter = 0;
			if (getNewPulse) {
				getNewPulse = 0;
				unsigned short data = pulseCount;
				//Write_Register(CONTROL_REG, 0);
				Write_Register(CONTROL_REG, SW_RESET);
				__delay_cycles(4000000);
				NDEF_Application_Data2[35] = (data / 1000) + 0x30;
				NDEF_Application_Data2[36] = ((data % 1000) / 100) + 0x30;
				NDEF_Application_Data2[37] = ((data % 100) / 10) + 0x30;
				NDEF_Application_Data2[38] = (data % 10) + 0x30;
				Write_Continuous(0, NDEF_Application_Data2, 40);
				Write_Register(CONTROL_REG, RF_ENABLE);
				__delay_cycles(4000000);
			} else {
				__delay_cycles(1000000);
			}
		}
		if (IE_heartbeat) {
			P1IE &= ~(BIT3);
			P1IFG &= ~(BIT3);
			IE_heartbeat = 0;
		}
		//Write_Register(CONTROL_REG, 0);
		Write_Register(CONTROL_REG, SW_RESET);
		__delay_cycles(4000000);
	}
	return 0;
}

#pragma vector=WDT_VECTOR
__interrupt void watchdog_timer(void) {
	pulseTimerCounter++;
}

#pragma vector=PORT1_VECTOR
__interrupt void Port1_ISR(void) {
	if (P1IFG & BIT3) {
		//get the heartbeat pulses
		getNewPulse = 1;
		pulseCount = pulseTimerCounter;
		pulseTimerCounter = 0;
		P1IFG &= ~(BIT3); //clear interrupt flag
	}
}
/*
#pragma vector=PORT2_VECTOR
__interrupt void PORT2_ISR(void) {
	//INTO interrupt fired
	if (P2IFG & BIT4) {
		P2IFG &= ~(BIT4); //clear interrupt flag
	}
}*/

