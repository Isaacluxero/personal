#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "snake_utils.h"
#include "state.h"

/* Helper function definitions */
static char get_board_at(game_state_t* state, int x, int y);
static void set_board_at(game_state_t* state, int x, int y, char ch);
static bool is_tail(char c);
static bool is_snake(char c);
static char body_to_tail(char c);
static int incr_x(char c);
static int incr_y(char c);
static void find_head(game_state_t* state, int snum);
static char next_square(game_state_t* state, int snum);
static void update_tail(game_state_t* state, int snum);
static void update_head(game_state_t* state, int snum);

/* Helper function to get a character from the board (already implemented for you). */
static char get_board_at(game_state_t* state, int x, int y) {
  return state->board[y][x];
}

/* Helper function to set a character on the board (already implemented for you). */
static void set_board_at(game_state_t* state, int x, int y, char ch) {
  state->board[y][x] = ch;
}

/* Task 1 */
game_state_t* create_default_state() {
  game_state_t* state_ptr;
  state_ptr = malloc(sizeof(game_state_t));

  unsigned int x_size = 14u, y_size = 10u;
  state_ptr->x_size = x_size; 
  state_ptr->y_size = y_size;

  char** board_ptr = malloc(sizeof(char *) * y_size); // heap


  int i = 0;
  board_ptr[0] = malloc((x_size + 2) * sizeof(char));
  for (i = 0; i < x_size; i++) {
    board_ptr[0][i] = ' ';
  }
  board_ptr[0][x_size] = '\n';
  board_ptr[0][x_size + 1] = '\0';

  i = 1; // starts at 1 because manyally setting board_ptr[0]
  while (i < y_size)
  // allocating x_size space y_size times in order to store chars
  {
    board_ptr[i] = malloc((x_size + 2) * sizeof(char));
    board_ptr[i] = strcpy(board_ptr[i], board_ptr[0]);
    i++;
  }
  
  state_ptr->board = board_ptr;

  set_board_at(state_ptr, 4, 4, 'd');
  set_board_at(state_ptr, 5, 4, '>');
  set_board_at(state_ptr, 9, 2, '*');

  for (i = 0; i < y_size; i++)
  {
    set_board_at(state_ptr, 0, i, '#');
    set_board_at(state_ptr, 13, i, '#');
  }
  for (i = 0; i < x_size; i++)
  {
    set_board_at(state_ptr, i, 0, '#');
    set_board_at(state_ptr, i, 9, '#');
  }

  state_ptr->num_snakes = 1;
  state_ptr->snakes = malloc(sizeof(snake_t));

  if (state_ptr->snakes == NULL) {
    free(state_ptr);
  }

  state_ptr->snakes[0].tail_x = 4;
  state_ptr->snakes[0].tail_y = 4;
  state_ptr->snakes[0].head_x = 5;;
  state_ptr->snakes[0].head_y = 4;
  state_ptr->snakes[0].live = true;

  return state_ptr;
}

/* Task 2 */
void free_state(game_state_t* state) {
  for (int i = 0; i < state->y_size; i++) {
    free(state->board[i]);
  }
  free(state->board);
  free(state->snakes);
  free(state);
  return;
}

/* Task 3 */
void print_board(game_state_t* state, FILE* fp) { 
  unsigned int i, j;

  for (j = 0; j < state->y_size; j++) {
    for (i = 0; i < state->x_size; i++) {
      fprintf(fp, "%c", get_board_at(state, i, j)); 
    }
    fprintf(fp, "\n");
  }
  return;
}

/* Saves the current state into filename (already implemented for you). */
void save_board(game_state_t* state, char* filename) {
  FILE* f = fopen(filename, "w");
  print_board(state, f);
  fclose(f);
  return;
}

/* Task 4.1 */
/* Returns true if c is part of the snake's tail. The snake's tail consists of these characters: wasd */
static bool is_tail(char c) {
  if (c == 'w' || c == 'a'|| c == 's' || c == 'd') {
    return true;
  }
  return false;
}

/* Returns true if c is part of the snake. The snake consists of these characters: wasd^<>vx */
static bool is_snake(char c) {
  bool tail = is_tail(c);
  // A snake can consist of any of the following chars: wasd^<>vx
  if (tail || c == '^' || c == '>' || c == '<' || c == 'v' || c == 'x') {
    return true;
  }
  return false;
}

/*Converts a character in the snake's body (^<>v) to the matching character representing 
the snake's tail (wasd).*/
static char body_to_tail(char c) {
  if (c == '^') {
    c = 'w';
  }
  else if (c == '<') {
    c = 'a';
  }
  else if (c == 'v') {
    c = 's';
  }
  else if (c == '>') {
    c = 'd';
  }
  return c;
}

/* Returns 1 if c is > or d. Returns -1 if c is < or a. Returns 0 otherwise. */
static int incr_x(char c) {
  if (c == '>' || c == 'd')
  {
    return 1;
  }
  else if (c == '<' || c == 'a') {
    return -1;
  }
  return 0;
}

/* Returns 1 if c is v or s. Returns -1 if c is ^ or w. Returns 0 otherwise. */
static int incr_y(char c) {
  if (c == 'v' || c == 's') {
    return 1;
  }
  else if (c == '^' || c == 'w') {
    return -1;
  }
  return 0;
}

/* Task 4.2 */
static char next_square(game_state_t* state, int snum) {
  if (snum > state->num_snakes) {
    return '\0';
  }

  int x_coord = state->snakes[snum].head_x;
  int y_coord = state->snakes[snum].head_y;

  char c = get_board_at(state, x_coord, y_coord);

  int x_incr = incr_x(c);
  int y_incr = incr_y(c);

  return get_board_at(state, x_coord + x_incr, y_coord + y_incr);
}

/* Task 4.3 */
static void update_head(game_state_t* state, int snum) {
  if (snum > state->num_snakes) {
    return;
  }

  char s_head_x = state->snakes[snum].head_x;
  char s_head_y = state->snakes[snum].head_y;

  char head_char = get_board_at(state, s_head_x, s_head_y);

  state->snakes[snum].head_x += incr_x(head_char);
  state->snakes[snum].head_y += incr_y(head_char);
  
  set_board_at(state, state->snakes[snum].head_x, state->snakes[snum].head_y, head_char);
  
  return;
}

/* Task 4.4 */
static void update_tail(game_state_t* state, int snum) {
  if (snum > state->num_snakes)
  {
    return;
  }

  int s_tail_x = (int)state->snakes[snum].tail_x;
  int s_tail_y = (int)state->snakes[snum].tail_y;

  char tail_char = get_board_at(state, s_tail_x, s_tail_y);
  set_board_at(state, s_tail_x, s_tail_y, ' ');

  // increment tail x and y
  int x_incr = incr_x(tail_char);
  int y_incr = incr_y(tail_char);
 
  state->snakes[snum].tail_x = s_tail_x + x_incr;
  state->snakes[snum].tail_y = s_tail_y + y_incr;

  // change new tail to correct lettering
  tail_char = get_board_at(state, s_tail_x +x_incr, s_tail_y+y_incr);
  set_board_at(state, s_tail_x+x_incr, s_tail_y+y_incr, body_to_tail(tail_char));

  return;
}


/* Task 4.5 */
void update_state(game_state_t* state, int (*add_food)(game_state_t* state)) {
  int i = 0;

  // update each snake one by one
  for (i = 0; i < state->num_snakes; i++) {
    char next_sq = next_square(state, i);
    // if the snake runs into another snake or a wall
    if (next_sq == '#' || is_snake(next_sq)) {
      set_board_at(state, state->snakes[i].head_x, state->snakes[i].head_y, 'x');
      state->snakes[i].live = false;
    }
    // if the snake runs into a friut
    else if (next_sq == '*') {
      update_head(state, i);
      add_food(state);
    }
    // if the snake moves to an empty space
    else {
      update_head(state, i);
      update_tail(state, i);
    }
  }
  return;
}

/* Task 5 */
game_state_t* load_board(char* filename) {
  FILE* fp = fopen(filename, "r");

  if (fp == NULL) {
    return NULL;
  }

  // count how many rows and columns
  int x = 0, y = 0, i, j;
  int c;
  game_state_t* state;
  state = (game_state_t *)malloc(sizeof(game_state_t));

  do {
    c = fgetc(fp);
    x++;
  } while (c != '\n');
  state->x_size = (unsigned int)(x - 1);

  while (!feof(fp)) {
    for (i = 0; i < state->x_size; i++) {
      fgetc(fp);
    }
    y++;
  }
  state->y_size = (unsigned int)y;

  // fill in the board
  rewind(fp);
  
  char** board_ptr = malloc(sizeof(char*) * y);
  state->board = board_ptr;
  
  for(j = 0; j < state->y_size; j++)
  {
    board_ptr[j] = malloc((state->x_size + 1) * sizeof(char));
    for (i = 0; i < state->x_size + 1; i++) {
      c = fgetc(fp);
      set_board_at(state, i, j, (char)c);
    }
  }
  fclose(fp);
  return state;
}

/* Task 6.1 */
static void find_head(game_state_t* state, int snum) {
  unsigned int tx = state->snakes[snum].tail_x;
  unsigned int ty = state->snakes[snum].tail_y;  
  char curr_sq = get_board_at(state, tx, ty);
  char counter;
  
  do {

    counter = get_board_at(state, tx, ty);
    //printf("%d,%d\n", tx, ty);
    if (curr_sq == 'w' || curr_sq == '^' ){
      ty--;
    }
    else if (curr_sq == 's' || curr_sq == 'v' ){
      ty++;
    }
    else if (curr_sq == 'a' || curr_sq == '<' ){
      tx--;
    }
    else if (curr_sq == 'd' || curr_sq == '>' ){
      tx++;
   }
    else if (curr_sq == 'x') {
      break;
    }
    curr_sq = get_board_at(state, tx, ty); 
  } while(curr_sq != ' ' && curr_sq != '#' && curr_sq != '*');

  if (counter == 'w' || counter == '^' ){
    ty++;
  }
  else if (counter == 's' || counter == 'v' ){
    ty--;
  }
  else if (counter == 'a' || counter == '<' ){
    tx++;
  }
  else if (counter == 'd' || counter == '>' ){
    tx--;
  }

  state->snakes[snum].head_y = ty;
  state-> snakes[snum].head_x = tx;
  printf("%c\n", curr_sq);
  printf("%c\n", counter);
  return;

}

/* Task 6.2 */
game_state_t* initialize_snakes(game_state_t* state) {
  unsigned int j = 0;
  unsigned int i = 0;
  unsigned int z = 0;
  unsigned int y = 0;
  unsigned int i1 = 0;
  unsigned int counter = 0;
  char curr;
  for (j = 0; j < state->y_size; j++) {
    for (i = 0; i < state->x_size; i++) {
      curr = get_board_at(state, i, j);
      if (is_tail(curr)){
        counter++;
      }
    }
  }
  
  state->num_snakes = counter;
  snake_t* snakes;
  snakes = malloc(sizeof(snake_t) * counter);
  state->snakes = snakes;
   //loop though to get tail coordinates
  for (y = 0; y < state->y_size; y++) {
    for (z = 0; z < state->x_size; z++) {
      curr = get_board_at(state, z, y);
      if (is_tail(curr)) {
        state->snakes[i1].tail_x = z;
        state->snakes[i1].tail_y = y;
        find_head(state, i1);
        i1++;
    }
  }
}
return state;
}
