
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <signal.h>
#include <sys/time.h>
#include <string.h>

#define CHAIR_COUNT 10
#define SELLERS_COUNT 10    // number of sellers there are total
#define SELLING_DURATION 60

typedef int bool;
#define true 1
#define false 0

int seats[CHAIR_COUNT*CHAIR_COUNT];      // 100 spots available in concert
pthread_mutex_t chairMutex;             // mutex protects seats and wait count

int totalSales = 0;

pthread_mutex_t printMutex;             // mutex protects printing
sem_t filledseats;                     // seller sells until this semaphore indicates all seats have been taken

struct itimerval sellersTimer;             // seller's selling timer
time_t startTime;

int startids[10] = {1,1,1,1,1,1,1,1,1,1};

int timesUp = 0;  // 1 = selling is over

char* sellersArr[10] = {"H0", "M1", "M2", "M3", "L1", "L2", "L3", "L4", "L5", "L6"};

/*** CREATE INS AND OUTS FOR EACH QUEUEUEUE ***/
int queueIn[10] = {0,0,0,0,0,0,0,0,0,0};
int queueOut[10] = {0,0,0,0,0,0,0,0,0,0};

int waitCounts[10] =    {0,0,0,0,0,0,0,0,0,0};
int soldCounts[10] =    {0,0,0,0,0,0,0,0,0,0};
int arrivedCounts[10] = {0,0,0,0,0,0,0,0,0,0};
int custId= 0;

int hSeat = 0;  //current seat for H customers
int mSeat = 40; //current seat for M customers
int lSeat = 90; //current seat for L customers

int* sellersQueues[10] = {NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL};

int N;

// Print a line for each event:
void print(char *event, bool showChart)
{
    time_t now;
    time(&now);
    double elapsed = difftime(now, startTime);
    int min = 0;
    int sec = (int) elapsed;

    if (sec >= 60) {
        min++;
        sec -= 60;
    }

        // Acquire the mutex lock to protect the printing.

    pthread_mutex_lock(&printMutex);
    
        // Elapsed time.
    printf("%1d:%02d - ", min, sec);

    printf("%s\n", event);
    printf("-----------------------------------------------------------\n");

    if (showChart){
    int c;
        // print seating chart
    for (c = 0; c < CHAIR_COUNT*CHAIR_COUNT; c++) {
        if(c % 10==0){
            printf("\n");
        }
        int oneSeat = seats[c];
        int theSellerOfSeat = oneSeat%10;

        if (oneSeat >= 0){
            printf("%2s%02d ", sellersArr[theSellerOfSeat], oneSeat/10);

        }
        else{
            printf("  -  ");
        }
    }

        printf("\n-----------------------------------------------------------\n");
    }
        // Release the mutex lock.

    pthread_mutex_unlock(&printMutex);

}

    // A customer arrives.
void customerArrives(int id, int seller)
{
    char event[80];

        // Acquire the mutex lock to protect the seats and the wait count.
    pthread_mutex_lock(&chairMutex);

    // find the sellers queue, and add a customer to it.
    int * sellerQueue = sellersQueues[seller];
    sellerQueue[queueIn[seller]] = id;

    waitCounts[seller]++;
    arrivedCounts[seller]++;

    queueIn[seller] = (queueIn[seller]+1)%N; // inc index in circular queue where customer was added

    sprintf(event, "A customer arrives in seller %s\'s queue",sellersArr[seller]);
    print(event, false);

        // Release the mutex lock.
    pthread_mutex_unlock(&chairMutex);

}

int numRandom(int high, int low)
{
    return rand() % (high-low+1) + low;
}

    // The customer thread.
void *customer(void *param)
{
    if(timesUp == 0){
        int id = *((int *) param);
        int temp = id%10; // temp is a value from 0 - 9

            // customers will arrive at random times during the selling.
        sleep(numRandom(59,0));
        customerArrives(id, temp);
    }
    return NULL;
}


    // The seller meets a customer if one is in their queue
void Meetscustomer(int sellerId)
{
    int* nextAvailSeat= NULL;

    if (totalSales == CHAIR_COUNT * CHAIR_COUNT && waitCounts[sellerId] != 0){
        pthread_mutex_lock(&chairMutex);
        char event[80];
        sprintf(event, "seller %s tells customer concert is sold out; They leave",sellersArr[sellerId]);
        print(event, false);
        waitCounts[sellerId]--;
        pthread_mutex_unlock(&chairMutex);

    }

    if (timesUp == 0 && totalSales != CHAIR_COUNT*CHAIR_COUNT && waitCounts[sellerId] != 0) {

        char event[80];
        sprintf(event, "seller %s serves their next customer in line",sellersArr[sellerId]);
        print(event, false);

        if (sellerId == 0){
            sleep(numRandom(2, 1));
        }else if (sellerId == 1 || sellerId == 2 || sellerId == 3){
            sleep(numRandom(4, 2));
        }else{
            sleep(numRandom(7, 4));
        }

        pthread_mutex_lock(&chairMutex);

        custId = (sellersQueues[sellerId])[queueOut[sellerId]];    // get their next customer
        (queueOut[sellerId])= ((queueOut[sellerId])+1)%N;       // take them out of queue
        (waitCounts[sellerId])--;

        switch (sellerId) {
            case 0:
                nextAvailSeat = &hSeat;
                break;
            case 1: // M sellers
                nextAvailSeat = &mSeat;
                break;
            case 2:
                nextAvailSeat = &mSeat;
                break;
            case 3:
                nextAvailSeat = &mSeat;
                break;
            default: // L sellers
                nextAvailSeat = &lSeat;
                break;
        }//end switch

            // find the next avail. seat!
        while(totalSales != CHAIR_COUNT*CHAIR_COUNT && seats[*nextAvailSeat] != -1 ){//if the seat isn't free

            if (sellerId == 0)//H seller
            {
                (*nextAvailSeat) = ((*nextAvailSeat)+1)%100;
            }
            else if (sellerId == 1 || sellerId == 2 || sellerId == 3 )//M seller
            {
                (*nextAvailSeat) = ((*nextAvailSeat)+1);

                if((*nextAvailSeat) == 60)
                    (*nextAvailSeat) = 30;
                else if((*nextAvailSeat) == 40)
                    (*nextAvailSeat) = 60;
                else if((*nextAvailSeat) == 70)
                    (*nextAvailSeat) = 20;
                else if((*nextAvailSeat) == 30)
                    (*nextAvailSeat) = 70;
                else if((*nextAvailSeat) == 80)
                    (*nextAvailSeat) = 10;
                else if((*nextAvailSeat) == 20)
                    (*nextAvailSeat) = 80;
                else if((*nextAvailSeat) == 90)
                    (*nextAvailSeat) = 0;
                else if((*nextAvailSeat) == 10)
                    (*nextAvailSeat) = 0;
            }
            else //L seller
            {
                    //start from 90 -> 100, then go to 80 -> 90 etc...
                (*nextAvailSeat)++;
                if(*nextAvailSeat % 10 == 0)
                    *nextAvailSeat = *nextAvailSeat - 20;
            }
        }

            // there is a spot for them in the concert!!
        if (totalSales != CHAIR_COUNT*CHAIR_COUNT && seats[*nextAvailSeat] == -1){
            int oneSeat = (startids[sellerId] * 10) + sellerId;
            (startids[sellerId])++;

            seats[*nextAvailSeat] = oneSeat; // the seller who sold this seat!
            (soldCounts[sellerId])++;
            totalSales++;

            if (!timesUp){
                sprintf(event, "seller %s sells a seat to customer", sellersArr[sellerId]);
            }else{
            sprintf(event, "seller %s finishes up last transaction and closes", sellersArr[sellerId]);
             
            }
            print(event, true);

        }

        custId = 0;
        pthread_mutex_unlock(&chairMutex);

    }

}//end Meetscustomer

    // The seller thread.
void *seller(void *param)
{
    int sellerId = *((int *) param);
        // Meet customers until the selling time is over.
    do {
        Meetscustomer(sellerId);
    } while (timesUp == 0);

    return NULL;
}

    // Timer signal handler.
void timerHandler(int signal)
{
    timesUp = 1;  // selling is over
}

void customersGoAway(int* waitCount){
    while ((*waitCount) > 0) {
        (*waitCount)--;
    }
}

    // Main.
int main(int argc, char *argv[])
{
    int i;

    if(argc < 2)
    {
        N = 5; // default val if none given
        printf("No N Given... Running with N = %d\n", N);
    } else {
        N = atoi(argv[1]);
        printf("Running with N = %d\n", N);
    }
        //set every seat to null to begin
    int s;
    for (s = 0; s < CHAIR_COUNT*CHAIR_COUNT; s++) {
        seats[s] = -1;
    }

    time(&startTime);
                // Set the timer for for selling duration.
    sellersTimer.it_value.tv_sec = SELLING_DURATION;
    setitimer(ITIMER_REAL, &sellersTimer, NULL);


        // allocate space for all the queues
    for (i = 0; i < SELLERS_COUNT; i++){
        sellersQueues[i] = (int*) malloc(sizeof(int) * N);
    }

    int customerIds[10 * N];
    int HsellerId  = 0;
    int M1sellerId = 1;
    int M2sellerId = 2;
    int M3sellerId = 3;

    int L1sellerId = 4;
    int L2sellerId = 5;
    int L3sellerId = 6;
    int L4sellerId = 7;
    int L5sellerId = 8;
    int L6sellerId = 9;

        // Initialize the mutexes and the semaphore.
    pthread_mutex_init(&chairMutex, NULL);
    pthread_mutex_init(&printMutex, NULL);
    sem_init(&filledseats, 0, 0);

        // srand(0);
    srand(time(0));

    /*** CREATE SELLER THREADS ***/
        // Create the H seller thread.
    pthread_t HsellerThreadId;
    pthread_attr_t HAttr;
    pthread_attr_init(&HAttr);
    pthread_create(&HsellerThreadId, &HAttr, seller, &HsellerId);

        // Create the M1 seller thread.
    pthread_t M1sellerThreadId;
    pthread_attr_t M1Attr;
    pthread_attr_init(&M1Attr);
    pthread_create(&M1sellerThreadId, &M1Attr, seller, &M1sellerId);

        // Create the M2 seller thread.
    pthread_t M2sellerThreadId;
    pthread_attr_t M2Attr;
    pthread_attr_init(&M2Attr);
    pthread_create(&M2sellerThreadId, &M2Attr, seller, &M2sellerId);

        // Create the M3 seller thread.
    pthread_t M3sellerThreadId;
    pthread_attr_t M3Attr;
    pthread_attr_init(&M3Attr);
    pthread_create(&M3sellerThreadId, &M3Attr, seller, &M3sellerId);

        // Create the L1 seller thread.
    pthread_t L1sellerThreadId;
    pthread_attr_t L1Attr;
    pthread_attr_init(&L1Attr);
    pthread_create(&L1sellerThreadId, &L1Attr, seller, &L1sellerId);

        // Create the L2 seller thread.
    pthread_t L2sellerThreadId;
    pthread_attr_t L2Attr;
    pthread_attr_init(&L2Attr);
    pthread_create(&L2sellerThreadId, &L2Attr, seller, &L2sellerId);

        // Create the L3 seller thread.
    pthread_t L3sellerThreadId;
    pthread_attr_t L3Attr;
    pthread_attr_init(&L3Attr);
    pthread_create(&L3sellerThreadId, &L3Attr, seller, &L3sellerId);

        // Create the L4 seller thread.
    pthread_t L4sellerThreadId;
    pthread_attr_t L4Attr;
    pthread_attr_init(&L4Attr);
    pthread_create(&L4sellerThreadId, &L4Attr, seller, &L4sellerId);

        // Create the L2 seller thread.
    pthread_t L5sellerThreadId;
    pthread_attr_t L5Attr;
    pthread_attr_init(&L5Attr);
    pthread_create(&L5sellerThreadId, &L5Attr, seller, &L5sellerId);

        // Create the L6 seller thread.
    pthread_t L6sellerThreadId;
    pthread_attr_t L6Attr;
    pthread_attr_init(&L6Attr);
    pthread_create(&L6sellerThreadId, &L6Attr, seller, &L6sellerId);
    /*** END CREATE SELLER THREADS ***/


        // Create the customer threads for ALL
    for (i = 0; i < N * 10; i++) {
        customerIds[i] = i;
        pthread_t customerThreadId;
        pthread_attr_t customerAttr;
        pthread_attr_init(&customerAttr);
        pthread_create(&customerThreadId, &customerAttr, customer, &customerIds[i]);
    }

        // Set the timer signal handler.
    signal(SIGALRM, timerHandler);

        // Wait for the seller to complete the selling.
    pthread_join(HsellerThreadId,  NULL);
    pthread_join(M1sellerThreadId, NULL);
    pthread_join(M2sellerThreadId, NULL);
    pthread_join(M3sellerThreadId, NULL);
    pthread_join(L1sellerThreadId, NULL);
    pthread_join(L2sellerThreadId, NULL);
    pthread_join(L3sellerThreadId, NULL);
    pthread_join(L4sellerThreadId, NULL);
    pthread_join(L5sellerThreadId, NULL);
    pthread_join(L6sellerThreadId, NULL);

        // Remaining waiting customers leave.
    custId = 0;

    printf("**** ALL OTHER CUSTOMERS LEAVE **** \n");
    for (i = 0; i < SELLERS_COUNT; i++){
        customersGoAway(&waitCounts[i]);
        queueIn[i] = 0;
        queueOut[i] = 0;
    }

    print("Finished with all pending transactions... ", true);
    
    printf("\n\n***** RESULTS *****\n");

    // Final statistics and free memory.
    int j;
    for(j = 0; j < SELLERS_COUNT; j++){
        printf("\n");
        printf("%5d %s customers arrived\n", arrivedCounts[j], sellersArr[j]);
        printf("%5d %s customers were sold tickets\n", soldCounts[j], sellersArr[j]);
        printf("%5d %s customers were turned away\n", arrivedCounts[j] - soldCounts[j], sellersArr[j]);
        free(sellersQueues[j]);
    }

    return 0;
}
