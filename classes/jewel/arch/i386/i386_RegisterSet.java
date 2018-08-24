/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Reg;

public interface i386_RegisterSet {

  /* segment registers */
  public static final int _ES = 0;
  public static final int _CS = 1;
  public static final int _SS = 2;
  public static final int _DS = 3;
  public static final int _FS = 4;
  public static final int _GS = 5;

  /* 8-bit general purpose registers */
  public static final int _AL = 6;
  public static final int _CL = 7;
  public static final int _DL = 8;
  public static final int _BL = 9;
  public static final int _AH = 10;
  public static final int _CH = 11;
  public static final int _DH = 12;
  public static final int _BH = 13;

  /* 16-bit general purpose registers */
  public static final int _AX = 14;
  public static final int _CX = 15;
  public static final int _DX = 16;
  public static final int _BX = 17;
  public static final int _SP = 18;
  public static final int _BP = 19;
  public static final int _SI = 20;
  public static final int _DI = 21;

  /* 32-bit general purpose registers */
  public static final int _EAX = 22;
  public static final int _ECX = 23;
  public static final int _EDX = 24;
  public static final int _EBX = 25;
  public static final int _ESP = 26;
  public static final int _EBP = 27;
  public static final int _ESI = 28;
  public static final int _EDI = 29;

  /* control registers */
  public static final int _CR0 = 30;
  public static final int _CR1 = 31;
  public static final int _CR2 = 32;
  public static final int _CR3 = 33;
  public static final int _CR4 = 34;
  public static final int _CR5 = 35;
  public static final int _CR6 = 36;
  public static final int _CR7 = 37;

  /* debug registers */
  public static final int _DR0 = 38;
  public static final int _DR1 = 39;
  public static final int _DR2 = 40;
  public static final int _DR3 = 41;
  public static final int _DR4 = 42;
  public static final int _DR5 = 43;
  public static final int _DR6 = 44;
  public static final int _DR7 = 45;

  /* test registers */
  public static final int _TR0 = 46;
  public static final int _TR1 = 47;
  public static final int _TR2 = 48;
  public static final int _TR3 = 49;
  public static final int _TR4 = 50;
  public static final int _TR5 = 51;
  public static final int _TR6 = 52;
  public static final int _TR7 = 53;

  /* floating point rstack registers */
  public static final int _ST0 = 54;
  public static final int _ST1 = 55;
  public static final int _ST2 = 56;
  public static final int _ST3 = 57;
  public static final int _ST4 = 58;
  public static final int _ST5 = 59;
  public static final int _ST6 = 60;
  public static final int _ST7 = 61;

  /* lower limit for virtual registers */
  public static final int _VR0 = 62;

  /* segment registers */
  public static final Reg ES = new i386_Reg(_ES, "es");
  public static final Reg CS = new i386_Reg(_CS, "cs");
  public static final Reg SS = new i386_Reg(_SS, "ss");
  public static final Reg DS = new i386_Reg(_DS, "ds");
  public static final Reg FS = new i386_Reg(_FS, "fs");
  public static final Reg GS = new i386_Reg(_GS, "gs");

  /* 8-bit general purpose registers */
  public static final Reg AL = new i386_Reg(_AL, "al");
  public static final Reg CL = new i386_Reg(_CL, "cl");
  public static final Reg DL = new i386_Reg(_DL, "dl");
  public static final Reg BL = new i386_Reg(_BL, "bl");
  public static final Reg AH = new i386_Reg(_AH, "ah");
  public static final Reg CH = new i386_Reg(_CH, "ch");
  public static final Reg DH = new i386_Reg(_DH, "dh");
  public static final Reg BH = new i386_Reg(_BH, "bh");

  /* 16-bit general purpose registers */
  public static final Reg AX = new i386_Reg(_AX, "ax");
  public static final Reg CX = new i386_Reg(_CX, "cx");
  public static final Reg DX = new i386_Reg(_DX, "dx");
  public static final Reg BX = new i386_Reg(_BX, "bx");
  public static final Reg SP = new i386_Reg(_SP, "sp");
  public static final Reg BP = new i386_Reg(_BP, "bp");
  public static final Reg SI = new i386_Reg(_SI, "si");
  public static final Reg DI = new i386_Reg(_DI, "di");

  /* 32-bit general purpose registers */
  public static final Reg EAX = new i386_Reg(_EAX, "eax");
  public static final Reg ECX = new i386_Reg(_ECX, "ecx");
  public static final Reg EDX = new i386_Reg(_EDX, "edx");
  public static final Reg EBX = new i386_Reg(_EBX, "ebx");
  public static final Reg ESP = new i386_Reg(_ESP, "esp");
  public static final Reg EBP = new i386_Reg(_EBP, "ebp");
  public static final Reg ESI = new i386_Reg(_ESI, "esi");
  public static final Reg EDI = new i386_Reg(_EDI, "edi");

  /* control registers */
  public static final Reg CR0 = new i386_Reg(_CR0, "cr0");
  public static final Reg CR1 = new i386_Reg(_CR1, "cr1");
  public static final Reg CR2 = new i386_Reg(_CR2, "cr2");
  public static final Reg CR3 = new i386_Reg(_CR3, "cr3");
  public static final Reg CR4 = new i386_Reg(_CR4, "cr4");
  public static final Reg CR5 = new i386_Reg(_CR5, "cr5");
  public static final Reg CR6 = new i386_Reg(_CR6, "cr6");
  public static final Reg CR7 = new i386_Reg(_CR7, "cr7");

  /* debug registers */
  public static final Reg DR0 = new i386_Reg(_DR0, "dr0");
  public static final Reg DR1 = new i386_Reg(_DR1, "dr1");
  public static final Reg DR2 = new i386_Reg(_DR2, "dr2");
  public static final Reg DR3 = new i386_Reg(_DR3, "dr3");
  public static final Reg DR4 = new i386_Reg(_DR4, "dr4");
  public static final Reg DR5 = new i386_Reg(_DR5, "dr5");
  public static final Reg DR6 = new i386_Reg(_DR6, "dr6");
  public static final Reg DR7 = new i386_Reg(_DR7, "dr7");

  /* test registers */
  public static final Reg TR0 = new i386_Reg(_TR0, "tr0");
  public static final Reg TR1 = new i386_Reg(_TR1, "tr1");
  public static final Reg TR2 = new i386_Reg(_TR2, "tr2");
  public static final Reg TR3 = new i386_Reg(_TR3, "tr3");
  public static final Reg TR4 = new i386_Reg(_TR4, "tr4");
  public static final Reg TR5 = new i386_Reg(_TR5, "tr5");
  public static final Reg TR6 = new i386_Reg(_TR6, "tr6");
  public static final Reg TR7 = new i386_Reg(_TR7, "tr7");

  /* floating pointing stack registers */
  public static final Reg ST0 = new i386_Reg(_ST0, "st(0)");
  public static final Reg ST1 = new i386_Reg(_ST1, "st(1)");
  public static final Reg ST2 = new i386_Reg(_ST2, "st(2)");
  public static final Reg ST3 = new i386_Reg(_ST3, "st(3)");
  public static final Reg ST4 = new i386_Reg(_ST4, "st(4)");
  public static final Reg ST5 = new i386_Reg(_ST5, "st(5)");
  public static final Reg ST6 = new i386_Reg(_ST6, "st(6)");
  public static final Reg ST7 = new i386_Reg(_ST7, "st(7)");

}

