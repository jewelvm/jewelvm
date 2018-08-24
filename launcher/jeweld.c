/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

int main(int argc, char *argv[])
{
  int jargi;
  char **jargv;
  int argi;
  char *path;
  char *name;

  jargv = (char**)calloc(3+argc+1, sizeof(char*));
  if (jargv == NULL) {
    fprintf(stderr, "%s: %s\n", "jeweld", "memory allocation failure");
    return -2;
  }

  jargi = 1;

  //jargv[jargi++] = "-classpath";
  //jargv[jargi++] = "jewel.jar"; // fix!!!

  for (argi = 1; argi < argc; argi++) {
    char *arg = argv[argi];
    if (strncmp(arg, "-J", 2) == 0)
      jargv[jargi++] = arg+2;
  }

  jargv[jargi++] = "jewel.core.Main";

  for (argi = 1; argi < argc; argi++) {
    char *arg = argv[argi];
    if (strncmp(arg, "-J", 2) != 0)
      jargv[jargi++] = arg;
  }

  path = strdup(argv[0]);
  if (path == NULL) {
    fprintf(stderr, "%s: %s\n", "jeweld", "memory allocation failure");
    return -2;
  }
  name = path;
  for (;;) {
    char *next = strstr(name+1, "jeweld");
    if (next == NULL)
      break;
    name = next;
  }
  strcpy(name, "jewel");
  
  execvp(jargv[0] = path, jargv);

  free(path);

  free(jargv);

  fprintf(stderr, "%s: %s\n", "jeweld", "could not find runtime system");

  return -3;
}

