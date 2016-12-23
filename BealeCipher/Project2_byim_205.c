//Ben Yim G00908659
//CS 262, Section 205

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

void fileInput(char fileName[], char word[][16]);
void cipher(char x[], char word[][16]);
void decode(char words[][16]);

int main()
{
	int userInput;
	char fileName[50];
	fileName[0] = 0;
	char words[5000][16];
	printf("Hello, and welcome to the Beale Cipher.\n");
	//Keep running loop until the user chooses to exit
	while(userInput != 4)
	{
		printf("Please choose a menu option to start (1-4):\n");
		printf("1. Enter a text file\n");
		printf("2. Create a cipher\n");
		printf("3. Decode an existing cipher\n");
		printf("4. Exit the program\n");
		scanf("%d", &userInput);
		//Checker for the correct menu option
		while(userInput != 1 && userInput != 2 && userInput != 3 && userInput != 4)
		{
			printf("Please enter the correct menu option:\n");
			printf("1. Enter a text file\n");
			printf("2. Create a cipher\n");
			printf("3. Decode an existing cipher\n");
			printf("4. Exit the program\n");
			scanf("%d", &userInput);
		}
		if(userInput == 1)
		{
			fileInput(fileName, words);
		}
		else if(userInput == 2)
		{
			//If file has not been initialized yet
			if(fileName[0] == 0)
			{
				printf("You forgot to enter an input text.\n");
				fileInput(fileName, words);
			}
			cipher(fileName, words);
		}
		else if(userInput == 3)
		{
			//If file has not been initialized yet
			if(fileName[0] == 0)
			{
				printf("You forgot to enter an input text.\n");
				fileInput(fileName, words);
			}
			decode(words);
		}
		else
		{
			printf("You are exiting the program.");
			exit(0);
		}
	}
	return 0;
}

void fileInput(char fileName[], char word[][16])
{
	FILE *inFile;
	printf("Please enter the file name (eg. name.txt):\n");
	scanf("%s", fileName);

	inFile = fopen(fileName, "r");
	if(inFile == NULL)
	{
		printf("This file does not exist. Ending program.\n");
		exit(0);
	}

	char line[256];
	//2d array for the words from the text file
	int wordCounter = 0;
	while(fgets(line, 256, (FILE*)inFile) != NULL)
	{
		char *token;
	    int col = 15;
	    char t[2] = " ";
	    int c2 = 0;
	    token = strtok(line, t);
	    //Put the words into the 2d array
	    while(token != NULL)
	    {
	        strcpy(word[wordCounter], token);
	        //Put to lower case
	        while(c2 < col)
	        {
	            char c = word[wordCounter][c2];
	            word[wordCounter][c2] = tolower(c);
	            c2++;   
	        }
	        token = strtok(NULL, t);
	        c2=0;
	        wordCounter++;
	    }
	}
	fclose(inFile);
}

void cipher(char fileName[], char words[][16])
{
	FILE *outFile;
	int messLength = 512;
	char mess[messLength];

	printf("Please enter the secret message:");
	scanf("%s", mess);

	char ciph[1600] = "";//string for ciphered message
	int row = 5000; //row value of words
	int seed = 8659;
	srandom(seed);
	int randNum = (random()%10);
	int matchCount = 0;
	int messIndex = 0; //index of message array
	char ex[10] = ""; //temporary storage

	int r = 0; //row index
	int i = 0; //word index
	int reset = 0; //boolean to see if program should loop to very beginning of words in string array
	int check = 0;
	//loop through all words
	while(r < row)
	{
		i = 0;
		//loop through all indexes of word
		while(i < 16)
		{
			if(words[r][i] == mess[messIndex])
			{
				matchCount++;
			}
			//If program went through whole text file and could not find the letter
			if(words[r][0]=='\0' && matchCount == 0)
			{
				strcat(ciph, "#");
				check = 1;
				messIndex++;
				//If we reached end of the entered secret message
				if(mess[messIndex] == '\0')
				{
					r = row;
					i = 16;
				}
				//There are still more letters in the secret message
				else
				{
					strcat(ciph, ",");
					i = 16;
					r = 0;
					reset = 1;
				}
			}

			//To reset the loop to the beginning of the file if it couldnt find enough instances
			if(words[r][0]=='\0' && matchCount != randNum+1)
			{
				r = 0;
				i = 16;
				reset = 1;
			}

			//If program found number of matches that equals the random number
			if(matchCount == randNum+1)
			{
				//Putting the numbers into the cipher string
				sprintf(ex, "%d", r);
				strcat(ciph, ex);
				strcat(ciph, ",");
				sprintf(ex, "%d", i);
				strcat(ciph, ex);
			
				randNum = (random()%10); //create new number
				matchCount = 0; //reset number of matches
				messIndex++;
				//If we reached end of the entered secret message
				if(mess[messIndex] == '\0')
				{
					i = 16;
					r = row;
				}
				//There are still more letters in the secret message
				else
				{
					r = 0;
					reset = 1;
					i = 16;
					strcat(ciph, ",");
				}
			}
			i++;
		}
		if(reset == 1)
		{
			reset = 0;
		}
		else
		{
			r++;
		}
	}
	//Writing ciphered message to file
	char outF[256];
	printf("Please enter a text file to write to: ");
	scanf("%s", outF);
	outFile = fopen(outF, "w");
	fprintf(outFile, "%s", ciph);

	fclose(outFile);
}

void decode(char words[][16])
{
	FILE *infile;
	char inF[256];
	printf("Please enter a text file to decode: ");
	scanf("%s", inF);

	infile = fopen(inF, "r");
	if(infile == NULL)
	{
		printf("This file does not exist. Ending program.");
		exit(0);
	}

	char mess[512]; //descrypted message
	char dec[500]; //encrypted message given from file
	int next = 0;
	char line[256];
	int messIndex = 0;
	
	while(fgets(line, 256, (FILE*)infile) != NULL)
	{
		strcpy(dec, line);
	}

	int i = 0;
	while(i < 500)
	{
		char temp[3] = "";
		char num[4] = ""; //array to get multiple number length from text file
		char num2[4] = ""; //same thing but for the second number in the pair
		int val1; 
		int val2; 
		int myNum=-1; //number found in text file
		int nextNum = 0; //boolean to check if program is searching for the second num
		int gotPair = 0; //boolean to check if a pair of numbers was found
		int addSpace = 0; //boolean to add a space to the message string
		while(gotPair == 0)
		{
			//checking for '#' or ' '
			if(dec[i] == '#')
			{
				mess[messIndex] = '#';
				messIndex++;
				gotPair = 1;
			}

			if(dec[i] == ' ')
			{
				//Set boolean to true to add a space to the message array
				addSpace = 1;
				gotPair = 1;
			}
			//if program is at end of pair of numbers
			if(dec[i] == ',' && nextNum == 1)
			{
				gotPair = 1;
			}
			//Set nextNum boolean to true because we found a ','
			if(dec[i] == ',')
			{
				nextNum = 1;
			}

			//To convert the char int to an int
			//Did it this way because I did not know how to do it another way
			switch(dec[i])
			{
				case '0':
					myNum = 0;
					break;
				case '1':
					myNum = 1;
					break;
				case '2':
					myNum = 2;
					break;
				case '3':
					myNum = 3;
					break;
				case '4':
					myNum = 4;
					break;
				case '5':
					myNum = 5;
					break;
				case '6':
					myNum = 6;
					break;
				case '7':
					myNum = 7;
					break;
				case '8':
					myNum = 8;
					break;
				case '9':
					myNum = 9;
					break;
			}
			//If myNum value got changed, meaning a number was found in the text file
			if(myNum != -1)
			{
				//First number in the pair
				if(nextNum == 0)
				{
					val1 = myNum;
					sprintf(temp, "%d", val1);
					strcat(num, temp);
				}
				//Second number in the pair
				if(nextNum == 1)
				{
					val2 = myNum;
					sprintf(temp, "%d", val2);
					strcat(num2, temp);
				}
			}
			myNum = -1;
			i++;
		}
		next = 0;
		val1 = atoi(num);
		val2 = atoi(num2);
		//check to make sure it fits in our word array
		if(val1 < 5000 && val2 < 16)
		{
			//Put the char at specified word and word index into the message string
			char curr = words[val1][val2];
			mess[messIndex] = curr;
		}
		messIndex++;

		//add a space to the message array
		if(addSpace == 1)
		{
			mess[messIndex] = ' ';
			messIndex++;
			addSpace = 0;
		}
		gotPair = 0;
	}
	fprintf(stdout, "M: %s\n", mess);
	fclose(infile);

}