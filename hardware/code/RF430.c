#include "msp430.h"
#include "RF430.h"

unsigned char RxData[2] = {0,0};
unsigned char TxData[2] = {0,0};
unsigned char TxAddr[2] = {0,0};

unsigned int Read_Register(unsigned int reg_addr)
{
	TxAddr[0] = reg_addr >> 8; 		//MSB of address
	TxAddr[1] = reg_addr & 0xFF; 	//LSB of address

	UCB0CTL1  &= ~UCSWRST;
	UCB0CTL1 |= UCTXSTT + UCTR;		//start i2c write operation
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[0];
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[1];
	while(!(IFG2 & UCB0TXIFG));
	UCB0CTL1 &= ~UCTR; 				//i2c read operation
	UCB0CTL1 |= UCTXSTT; 			//repeated start
	while(!(IFG2 & UCB0RXIFG));
	RxData[0] = UCB0RXBUF;
	UCB0CTL1 |= UCTXSTP; 			//send stop after next RX
	while(!(IFG2 & UCB0RXIFG));
	RxData[1] = UCB0RXBUF;
	while((UCB0STAT & UCBBUSY));    // Ensure stop condition got sent
	UCB0CTL1  |= UCSWRST;

	return RxData[1] << 8 | RxData[0];
}

//reads the register at reg_addr, returns the result
unsigned int Read_Register_BIP8(unsigned int reg_addr)
{
	unsigned char BIP8 = 0;
	TxAddr[0] = reg_addr >> 8; 		//MSB of address
	TxAddr[1] = reg_addr & 0xFF; 	//LSB of address

	UCB0CTL1  &= ~UCSWRST;
	UCB0CTL1 |= UCTXSTT + UCTR;		//start i2c write operation

	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[0];
	BIP8 ^= TxAddr[0];
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[1];
	BIP8 ^= TxAddr[1];

	while(!(IFG2 & UCB0TXIFG));    	// Waiting for TX to finish on bus
	UCB0CTL1 &= ~UCTR; 			   	//i2c read operation
	UCB0CTL1 |= UCTXSTT; 			//repeated start

	while(!(IFG2 & UCB0RXIFG));
	RxData[0] = UCB0RXBUF;
	BIP8 ^= RxData[0];
	while(!(IFG2 & UCB0RXIFG));
	RxData[1] = UCB0RXBUF;
	BIP8 ^= RxData[1];

	UCB0CTL1 |= UCTXSTP; 			//send stop after next RX
	while(!(IFG2 & UCB0RXIFG));
	if(BIP8 != UCB0RXBUF){
		__no_operation();
	}

	while((UCB0STAT & UCBBUSY));     // Ensure stop condition got sent
	UCB0CTL1  |= UCSWRST;

	return RxData[0] << 8 | RxData[1];
}

void Read_Continuous(unsigned int reg_addr, unsigned char* read_data, unsigned int data_length)
{
	unsigned int i;

	TxAddr[0] = reg_addr >> 8; 		//MSB of address
	TxAddr[1] = reg_addr & 0xFF; 	//LSB of address

	UCB0CTL1  &= ~UCSWRST;
	UCB0CTL1 |= UCTXSTT + UCTR;		//start i2c write operation.  Sending Slave address

	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[0];
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[1];
	while(!(IFG2 & UCB0TXIFG));    	// Waiting for TX to finish on bus
	UCB0CTL1 &= ~UCTR; 				//i2c read operation
	UCB0CTL1 |= UCTXSTT; 			//repeated start
	while(!(IFG2 & UCB0RXIFG));

	for(i = 0; i < data_length-1; i++)
	{
		while(!(IFG2 & UCB0RXIFG));
		read_data[i] = UCB0RXBUF;
	}

	UCB0CTL1 |= UCTXSTP; 			//send stop after next RX
	while(!(IFG2 & UCB0RXIFG));
	read_data[i] = UCB0RXBUF;
	while((UCB0STAT & UCBBUSY));    // Ensure stop condition got sent
	UCB0CTL1  |= UCSWRST;
}

//writes the register at reg_addr with value
void Write_Register(unsigned int reg_addr, unsigned int value)
{
	TxAddr[0] = reg_addr >> 8; 		//MSB of address
	TxAddr[1] = reg_addr & 0xFF; 	//LSB of address
	TxData[0] = value >> 8;
	TxData[1] = value & 0xFF;

	UCB0CTL1  &= ~UCSWRST;
	UCB0CTL1 |= UCTXSTT + UCTR;		//start i2c write operation
	//write the address
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[0];
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[1];
	//write the data
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxData[1];
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxData[0];
	while(!(IFG2 & UCB0TXIFG));
	UCB0CTL1 |= UCTXSTP;
	while((UCB0STAT & UCBBUSY));     // Ensure stop condition got sent
	UCB0CTL1  |= UCSWRST;

}

//writes the register at reg_addr with value
void Write_Register_BIP8(unsigned int reg_addr, unsigned int value)
{
	unsigned char BIP8 = 0;

	TxAddr[0] = reg_addr >> 8; 		//MSB of address
	TxAddr[1] = reg_addr & 0xFF; 	//LSB of address
	TxData[0] = value >> 8;
	TxData[1] = value & 0xFF;

	UCB0CTL1  &= ~UCSWRST;
	UCB0CTL1 |= UCTXSTT + UCTR;		//start i2c write operation

	//write the address
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[0];
	BIP8 ^= TxAddr[0];
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[1];
	BIP8 ^= TxAddr[1];

	//write the data
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxData[0];
	BIP8 ^= TxData[0];
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxData[1];
	BIP8 ^= TxData[1];

	//send BIP8 byte
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = BIP8;

	while(!(IFG2 & UCB0TXIFG));
	UCB0CTL1 |= UCTXSTP;
	while((UCB0STAT & UCBBUSY));;   // Ensure stop condition got sent
	UCB0CTL1  |= UCSWRST;

}

//writes the register at reg_addr and incrementing addresses with the data at "write_data" of length data_length
void Write_Continuous(unsigned int reg_addr, unsigned char* write_data, unsigned int data_length)
{
	unsigned int i;

	TxAddr[0] = reg_addr >> 8; 		//MSB of address
	TxAddr[1] = reg_addr & 0xFF; 	//LSB of address

	UCB0CTL1  &= ~UCSWRST;
	UCB0CTL1 |= UCTXSTT + UCTR;		//start i2c write operation
	//write the address
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[0];
	while(!(IFG2 & UCB0TXIFG));
	UCB0TXBUF = TxAddr[1];

	for(i = 0; i < data_length; i++)
	{
		while(!(IFG2 & UCB0TXIFG));
		UCB0TXBUF = write_data[i];
	}

	while(!(IFG2 & UCB0TXIFG));
	UCB0CTL1 |= UCTXSTP;
	while((UCB0STAT & UCBBUSY));    // Ensure stop condition got sent
	UCB0CTL1  |= UCSWRST;

}
