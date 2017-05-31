#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#include "conway.h"

void printusage(){
    printf("Usage: game <width> <height>\n");
}

void printboard(int **board, int width, int height){
    int i, j;

    for(i = 0; i < height; i++){
        for(j = 0; j < width; j++){
            printf("%d ", board[i][j]);
        }
        printf("\n");
    }

}

int main(int argc, char *argv[]){
    int width, height;
    int **board;
    int i;

    if(argc != 3){
        printusage();
        return 1;
    }
    if(sscanf(argv[1], "%d", &width) != 1){
        printusage();
        return 1;
    }

    if(sscanf(argv[2], "%d", &height) != 1){
        printusage();
        return 1;
    }

    /*allocate space for the board*/
    board = malloc(sizeof(int *) * height);
    for(i = 0; i < height; i++){
        board[i] = malloc(sizeof(int) * width);
    }

    /*initialize the board to be in a random state*/
    srand(time(NULL));

    for(i = 0; i < height; i++){
        int j;
        for(j = 0; j < width; j++){
            board[i][j] = rand() % 2;
        }
    }

    printboard(board, width, height);

    /*call your update method*/
    update(board, width, height);

    printf("\n");

    printboard(board, width, height);

    /*clean things up*/
    for(i = 0; i < height; i++){
        free(board[i]);
    }
    free(board);
}

