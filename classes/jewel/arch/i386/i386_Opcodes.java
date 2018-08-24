/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Opc;

public interface i386_Opcodes {

  /* CS/DS/ES/FS/GS/SS */
  public static final short _cs = 0;
  public static final short _ds = 1;
  public static final short _es = 2;
  public static final short _fs = 3;
  public static final short _gs = 4;
  public static final short _ss = 5;

  /* AAA */
  public static final short _aaa = 6;

  /* AAD */
  public static final short _aad = 7;

  /* AAM */
  public static final short _aam = 8;

  /* AAS */
  public static final short _aas = 9;

  /* ADC */
  public static final short _adcb = 10;
  public static final short _adcw = 11;
  public static final short _adcl = 12;
  
  /* ADD */
  public static final short _addb = 13;
  public static final short _addw = 14;
  public static final short _addl = 15;

  /* AND */
  public static final short _andb = 16;
  public static final short _andw = 17;
  public static final short _andl = 18;

  /* ARPL */
  public static final short _arpl = 19;

  /* BOUND */
  public static final short _boundw = 20;
  public static final short _boundl = 21;

  /* BSF */
  public static final short _bsfw = 22;
  public static final short _bsfl = 23;

  /* BSR */
  public static final short _bsrw = 24;
  public static final short _bsrl = 25;

  /* BT */
  public static final short _btw = 26;
  public static final short _btl = 27;

  /* BTC */
  public static final short _btcw = 28;
  public static final short _btcl = 29;

  /* BTR */
  public static final short _btrw = 30;
  public static final short _btrl = 31;

  /* BTS */
  public static final short _btsw = 32;
  public static final short _btsl = 33;

  /* CALL */
  public static final short _call = 34;
  public static final short _callw = 35;
  public static final short _calll = 36;
  public static final short _callf = 37;
  public static final short _callfw = 38;
  public static final short _callfl = 39;

  /* CBW/CWDE */
  public static final short _cbw = 40;
  public static final short _cwde = 41;

  /* CLC */
  public static final short _clc = 42;

  /* CLD */
  public static final short _cld = 43;

  /* CLI */
  public static final short _cli = 44;

  /* CLTS */
  public static final short _clts = 45;

  /* CMC */
  public static final short _cmc = 46;

  /* CMP */
  public static final short _cmpb = 47;
  public static final short _cmpw = 48;
  public static final short _cmpl = 49;

  /* CMPS/CMPSB/CMPSW/CMPSD */
  public static final short _cmpsb = 50;
  public static final short _cmpsw = 51;
  public static final short _cmpsd = 52;

  /* CWD/CDQ */
  public static final short _cwd = 53;
  public static final short _cdq = 54;

  /* DAA */
  public static final short _daa = 55;

  /* DAS */
  public static final short _das = 56;

  /* DEC */
  public static final short _decb = 57;
  public static final short _decw = 58;
  public static final short _decl = 59;

  /* DIV */
  public static final short _divb = 60;
  public static final short _divw = 61;
  public static final short _divl = 62;

  /* ENTER */
  public static final short _enter = 63;

  /* F2XM1 */
  public static final short _f2xm1 = 64;

  /* FABS */
  public static final short _fabs = 65;

  /* FADD/FADDP/FIADD */
  public static final short _fadds = 66;
  public static final short _faddl = 67;
  public static final short _fadd = 68;
  public static final short _faddp = 69;
  public static final short _fiaddw = 70;
  public static final short _fiaddl = 71;

  /* FBLD */
  public static final short _fbld = 72;

  /* FBSTP */
  public static final short _fbstp = 73;

  /* FCHS */
  public static final short _fchs = 74;

  /* FCLEX/FNCLEX */
  public static final short _fclex = 75;
  public static final short _fnclex = 76;

  /* FCOM/FCOMP/FCOMPP */
  public static final short _fcoms = 77;
  public static final short _fcoml = 78;
  public static final short _fcom = 79;
  public static final short _fcomps = 80;
  public static final short _fcompl = 81;
  public static final short _fcomp = 82;
  public static final short _fcompp = 83;

  /* FCOS */
  public static final short _fcos = 84;

  /* FDECSTP */
  public static final short _fdecstp = 85;
  
  /* FDIV/FDIVP/FIDIV */
  public static final short _fdivs = 86;
  public static final short _fdivl = 87;
  public static final short _fdiv = 88;
  public static final short _fdivp = 89;
  public static final short _fidivw = 90;
  public static final short _fidivl = 91;

  /* FDIVR/FDIVRP/FIDIVR */
  public static final short _fdivrs = 92;
  public static final short _fdivrl = 93;
  public static final short _fdivr = 94;
  public static final short _fdivrp = 95;
  public static final short _fidivrw = 96;
  public static final short _fidivrl = 97;

  /* FFREE */
  public static final short _ffree = 98;

  /* FICOM/FICOMP */
  public static final short _ficomw = 99;
  public static final short _ficoml = 100;
  public static final short _ficompw = 101;
  public static final short _ficompl = 102;
  
  /* FILD */
  public static final short _fildw = 103;
  public static final short _fildl = 104;
  public static final short _fildll = 105;

  /* FINCSTP */
  public static final short _fincstp = 106;

  /* FINIT/FNINIT */
  public static final short _finit = 107;
  public static final short _fninit = 108;

  /* FIST/FISTP */
  public static final short _fistw = 109;
  public static final short _fistl = 110;
  public static final short _fistpw = 111;
  public static final short _fistpl = 112;
  public static final short _fistpll = 113;

  /* FLD */
  public static final short _flds = 114;
  public static final short _fldl = 115;
  public static final short _fldt = 116;
  public static final short _fld = 117;

  /* FLD1/FLDL2T/FLDL2E/FLDPI/FLDLG2/FLDLN2/FLDZ */
  public static final short _fld1 = 118;
  public static final short _fldl2t = 119;
  public static final short _fldl2e = 120;
  public static final short _fldpi = 121;
  public static final short _fldlg2 = 122;
  public static final short _fldln2 = 123;
  public static final short _fldz = 124;

  /* FLDCW */
  public static final short _fldcw = 125;

  /* FLDENV */
  public static final short _fldenv = 126;

  /* FMUL/FMULP/FIMUL */
  public static final short _fmuls = 127;
  public static final short _fmull = 128;
  public static final short _fmul = 129;
  public static final short _fmulp = 130;
  public static final short _fimulw = 131;
  public static final short _fimull = 132;

  /* FNOP */
  public static final short _fnop = 133;

  /* FPATAN */
  public static final short _fpatan = 134;

  /* FPREM */
  public static final short _fprem = 135;

  /* FPREM1 */
  public static final short _fprem1 = 136;

  /* FPTAN */
  public static final short _fptan = 137;

  /* FRNDINT */
  public static final short _frndint = 138;

  /* FRSTOR */
  public static final short _frstor = 139;
  
  /* FSAVE/FNSAVE */
  public static final short _fsave = 140;
  public static final short _fnsave = 141;
  
  /* FSCALE */
  public static final short _fscale = 142;

  /* FSIN */
  public static final short _fsin = 143;

  /* FSINCOS */
  public static final short _fsincos = 144;

  /* FSQRT */
  public static final short _fsqrt = 145;

  /* FST/FSTP */
  public static final short _fsts = 146;
  public static final short _fstl = 147;
  public static final short _fst = 148;
  public static final short _fstps = 149;
  public static final short _fstpl = 150;
  public static final short _fstpt = 151;
  public static final short _fstp = 152;

  /* FSTCW/FNSTCW */
  public static final short _fstcw = 153;
  public static final short _fnstcw = 154;

  /* FSTENV/FNSTENV */
  public static final short _fstenv = 155;
  public static final short _fnstenv = 156;

  /* FSTSW/FNSTSW */
  public static final short _fstsw = 157;
  public static final short _fnstsw = 158;
  
  /* FSUB/FSUBP/FISUB */
  public static final short _fsubs = 159;
  public static final short _fsubl = 160;
  public static final short _fsub = 161;
  public static final short _fsubp = 162;
  public static final short _fisubw = 163;
  public static final short _fisubl = 164;

  /* FSUBR/FSUBRP/FISUBR */
  public static final short _fsubrs = 165;
  public static final short _fsubrl = 166;
  public static final short _fsubr = 167;
  public static final short _fsubrp = 168;
  public static final short _fisubrw = 169;
  public static final short _fisubrl = 170;

  /* FTST */
  public static final short _ftst = 171;

  /* FUCOM/FUCOMP/FUCOMPP */
  public static final short _fucom = 172;
  public static final short _fucomp = 173;
  public static final short _fucompp = 174;

  /* FXAM */
  public static final short _fxam = 175;
  
  /* FXCH */
  public static final short _fxch = 176;

  /* FXTRACT */
  public static final short _fxtract = 177;

  /* FYL2X */
  public static final short _fyl2x = 178;

  /* FYL2XP1 */
  public static final short _fyl2xp1 = 179;

  /* HLT */
  public static final short _hlt = 180;

  /* IDIV */
  public static final short _idivb = 181;
  public static final short _idivw = 182;
  public static final short _idivl = 183;

  /* IMUL */
  public static final short _imulb = 184;
  public static final short _imulw = 185;
  public static final short _imull = 186;

  /* IN */
  public static final short _inb = 187;
  public static final short _inw = 188;
  public static final short _inl = 189;

  /* INC */
  public static final short _incb = 190;
  public static final short _incw = 191;
  public static final short _incl = 192;

  /* INS/INSB/INSW/INSD */
  public static final short _insb = 193;
  public static final short _insw = 194;
  public static final short _insd = 195;

  /* INT/INTO */
  public static final short _inti = 196;
  public static final short _into = 197;

  /* IRET/IRETD */
  public static final short _iret = 198;
  public static final short _iretd = 199;

  /* Jcc */
  public static final short _ja = 200;
  public static final short _jae = 201;
  public static final short _jb = 202;
  public static final short _jbe = 203;
  public static final short _jc = 204;
  public static final short _jcxe = 205;
  public static final short _jcxz = 206;
  public static final short _jecxe = 207;
  public static final short _jecxz = 208;
  public static final short _je = 209;
  public static final short _jg = 210;
  public static final short _jge = 211;
  public static final short _jl = 212;
  public static final short _jle = 213;
  public static final short _jna = 214;
  public static final short _jnae = 215;
  public static final short _jnb = 216;
  public static final short _jnbe = 217;
  public static final short _jnc = 218;
  public static final short _jne = 219;
  public static final short _jng = 220;
  public static final short _jnge = 221;
  public static final short _jnl = 222;
  public static final short _jnle = 223;
  public static final short _jno = 224;
  public static final short _jnp = 225;
  public static final short _jns = 226;
  public static final short _jnz = 227;
  public static final short _jo = 228;
  public static final short _jp = 229;
  public static final short _jpe = 230;
  public static final short _jpo = 231;
  public static final short _js = 232;
  public static final short _jz = 233;

  /* JMP */
  public static final short _jmp = 234;
  public static final short _jmpw = 235;
  public static final short _jmpl = 236;
  public static final short _jmpf = 237;
  public static final short _jmpfw = 238;
  public static final short _jmpfl = 239;

  /* LAHF */
  public static final short _lahf = 240;

  /* LAR */
  public static final short _larw = 241;
  public static final short _larl = 242;

  /* LDS/LES/LFS/LGS/LSS */
  public static final short _ldsw = 243;
  public static final short _ldsl = 244;
  public static final short _lssw = 245;
  public static final short _lssl = 246;
  public static final short _lesw = 247;
  public static final short _lesl = 248;
  public static final short _lfsw = 249;
  public static final short _lfsl = 250;
  public static final short _lgsw = 251;
  public static final short _lgsl = 252;

  /* LEA */
  public static final short _leaw = 253;
  public static final short _leal = 254;

  /* LEAVE */
  public static final short _leave = 255;
  
  /* LGDT/LIDT */
  public static final short _lgdt = 256;
  public static final short _lidt = 257;

  /* LLDT */
  public static final short _lldt = 258;

  /* LMSW */
  public static final short _lmsw = 259;

  /* LOCK */
  public static final short _lock = 260;

  /* LODS/LODSB/LODSW/LODSD */
  public static final short _lodsb = 261;
  public static final short _lodsw = 262;
  public static final short _lodsd = 263;

  /* LOOP/LOOPcc */
  public static final short _loop = 264;
  public static final short _loope = 265;
  public static final short _loopz = 266;
  public static final short _loopne = 267;
  public static final short _loopnz = 268;
  
  /* LSL */
  public static final short _lslw = 269;
  public static final short _lsll = 270;

  /* LTR */
  public static final short _ltr = 271;

  /* MOV */
  public static final short _movb = 272;
  public static final short _movw = 273;
  public static final short _movl = 274;
  public static final short _movws = 275;
  public static final short _movsw = 276;

  /* MOVc */
  public static final short _movlc = 277;
  public static final short _movcl = 278;

  /* MOVg */
  public static final short _movlg = 279;
  public static final short _movgl = 280;

  /* MOVt */
  public static final short _movlt = 281;
  public static final short _movtl = 282;

  /* MOVSB/MOVSW/MOVSD */
  public static final short _movsb = 283;
  public static final short _movsd = 284;

  /* MOVSX */
  public static final short _movsxbw = 285;
  public static final short _movsxbl = 286;
  public static final short _movsxwl = 287;

  /* MOVZX */
  public static final short _movzxbw = 288;
  public static final short _movzxbl = 289;
  public static final short _movzxwl = 290;

  /* MUL */
  public static final short _mulb = 291;
  public static final short _mulw = 292;
  public static final short _mull = 293;

  /* NEG */
  public static final short _negb = 294;
  public static final short _negw = 295;
  public static final short _negl = 296;

  /* NOP */
  public static final short _nop = 297;

  /* NOT */
  public static final short _notb = 298;
  public static final short _notw = 299;
  public static final short _notl = 300;

  /* OR */
  public static final short _orb = 301;
  public static final short _orw = 302;
  public static final short _orl = 303;

  /* OUT */
  public static final short _outb = 304;
  public static final short _outw = 305;
  public static final short _outl = 306;

  /* OUTS/OUTSB/OUTSW/OUTSD */
  public static final short _outsb = 307;
  public static final short _outsw = 308;
  public static final short _outsd = 309;

  /* POP */
  public static final short _popw = 310;
  public static final short _popl = 311;
  public static final short _pops = 312;

  /* POPA/POPAD */
  public static final short _popa = 313;
  public static final short _popad = 314;

  /* POPF/POPFD */
  public static final short _popf = 315;
  public static final short _popfd = 316;

  /* PUSH */
  public static final short _pushw = 317;
  public static final short _pushl = 318;
  public static final short _pushs = 319;

  /* PUSHA/PUSHAD */
  public static final short _pusha = 320;
  public static final short _pushad = 321;

  /* PUSHF/PUSHFD */
  public static final short _pushf = 322;
  public static final short _pushfd = 323;

  /* RCL/RCR/ROL/ROR */
  public static final short _rclb = 324;
  public static final short _rclw = 325;
  public static final short _rcll = 326;
  public static final short _rcrb = 327;
  public static final short _rcrw = 328;
  public static final short _rcrl = 329;
  public static final short _rolb = 330;
  public static final short _rolw = 331;
  public static final short _roll = 332;
  public static final short _rorb = 333;
  public static final short _rorw = 334;
  public static final short _rorl = 335;

  /* REP/REPE/REPZ/REPNE/REPNZ */
  public static final short _rep = 336;
  public static final short _repe = 337;
  public static final short _repz = 338;
  public static final short _repne = 339;
  public static final short _repnz = 340;

  /* RET */
  public static final short _ret = 341;
  public static final short _retf = 342;

  /* SAHF */
  public static final short _sahf = 343;

  /* SAL/SAR/SHL/SHR */
  public static final short _salb = 344;
  public static final short _salw = 345;
  public static final short _sall = 346;
  public static final short _sarb = 347;
  public static final short _sarw = 348;
  public static final short _sarl = 349;
  public static final short _shlb = 350;
  public static final short _shlw = 351;
  public static final short _shll = 352;
  public static final short _shrb = 353;
  public static final short _shrw = 354;
  public static final short _shrl = 355;

  /* SBB */
  public static final short _sbbb = 356;
  public static final short _sbbw = 357;
  public static final short _sbbl = 358;

  /* SCASB/SCASW/SCASD */
  public static final short _scasb = 359;
  public static final short _scasw = 360;
  public static final short _scasd = 361;

  /* SETcc */
  public static final short _seta = 362;
  public static final short _setae = 363;
  public static final short _setb = 364;
  public static final short _setbe = 365;
  public static final short _setc = 366;
  public static final short _sete = 367;
  public static final short _setg = 368;
  public static final short _setge = 369;
  public static final short _setl = 370;
  public static final short _setle = 371;
  public static final short _setna = 372;
  public static final short _setnae = 373;
  public static final short _setnb = 374;
  public static final short _setnbe = 375;
  public static final short _setnc = 376;
  public static final short _setne = 377;
  public static final short _setng = 378;
  public static final short _setnge = 379;
  public static final short _setnl = 380;
  public static final short _setnle = 381;
  public static final short _setno = 382;
  public static final short _setnp = 383;
  public static final short _setns = 384;
  public static final short _setnz = 385;
  public static final short _seto = 386;
  public static final short _setp = 387;
  public static final short _setpe = 388;
  public static final short _setpo = 389;
  public static final short _sets = 390;
  public static final short _setz = 391;

  /* SGDT/SIDT */
  public static final short _sgdt = 392;
  public static final short _sidt = 393;

  /* SHLD */
  public static final short _shldw = 394;
  public static final short _shldl = 395;

  /* SHRD */
  public static final short _shrdw = 396;
  public static final short _shrdl = 397;

  /* SLDT */
  public static final short _sldt = 398;

  /* SMSW */
  public static final short _smsw = 399;

  /* STC */
  public static final short _stc = 400;

  /* STD */
  public static final short _std = 401;

  /* STI */
  public static final short _sti = 402;

  /* STOSB/STOSW/STOSD */
  public static final short _stosb = 403;
  public static final short _stosw = 404;
  public static final short _stosd = 405;

  /* STR */
  public static final short _str = 406;

  /* SUB */
  public static final short _subb = 407;
  public static final short _subw = 408;
  public static final short _subl = 409;

  /* TEST */
  public static final short _testb = 410;
  public static final short _testw = 411;
  public static final short _testl = 412;

  /* VERR/VERW */
  public static final short _verr = 413;
  public static final short _verw = 414;

  /* WAIT/FWAIT */
  public static final short _fwait = 415;

  /* XCHG */
  public static final short _xchgb = 416;
  public static final short _xchgw = 417;
  public static final short _xchgl = 418;

  /* XLATB */
  public static final short _xlatb = 419;

  /* XOR */
  public static final short _xorb = 420;
  public static final short _xorw = 421;
  public static final short _xorl = 422;

  /* CS/DS/ES/FS/GS/SS */
  public static final Opc cs = new i386_Opc(_cs, "cs");
  public static final Opc ds = new i386_Opc(_ds, "ds");
  public static final Opc es = new i386_Opc(_es, "es");
  public static final Opc fs = new i386_Opc(_fs, "fs");
  public static final Opc gs = new i386_Opc(_gs, "gs");
  public static final Opc ss = new i386_Opc(_ss, "ss");

  /* AAA */
  public static final Opc aaa = new i386_Opc(_aaa, "aaa");

  /* AAD */
  public static final Opc aad = new i386_Opc(_aad, "aad");

  /* AAM */
  public static final Opc aam = new i386_Opc(_aam, "aam");

  /* AAS */
  public static final Opc aas = new i386_Opc(_aas, "aas");

  /* ADC */
  public static final Opc adcb = new i386_Opc(_adcb, "adcb");
  public static final Opc adcw = new i386_Opc(_adcw, "adcw");
  public static final Opc adcl = new i386_Opc(_adcl, "adcl");
  
  /* ADD */
  public static final Opc addb = new i386_Opc(_addb, "addb");
  public static final Opc addw = new i386_Opc(_addw, "addw");
  public static final Opc addl = new i386_Opc(_addl, "addl");

  /* AND */
  public static final Opc andb = new i386_Opc(_andb, "andb");
  public static final Opc andw = new i386_Opc(_andw, "andw");
  public static final Opc andl = new i386_Opc(_andl, "andl");

  /* ARPL */
  public static final Opc arpl = new i386_Opc(_arpl, "arpl");

  /* BOUND */
  public static final Opc boundw = new i386_Opc(_boundw, "boundw");
  public static final Opc boundl = new i386_Opc(_boundl, "boundl");

  /* BSF */
  public static final Opc bsfw = new i386_Opc(_bsfw, "bsfw");
  public static final Opc bsfl = new i386_Opc(_bsfl, "bsfl");

  /* BSR */
  public static final Opc bsrw = new i386_Opc(_bsrw, "bsrw");
  public static final Opc bsrl = new i386_Opc(_bsrl, "bsrl");

  /* BT */
  public static final Opc btw = new i386_Opc(_btw, "btw");
  public static final Opc btl = new i386_Opc(_btl, "btl");

  /* BTC */
  public static final Opc btcw = new i386_Opc(_btcw, "btcw");
  public static final Opc btcl = new i386_Opc(_btcl, "btcl");

  /* BTR */
  public static final Opc btrw = new i386_Opc(_btrw, "btrw");
  public static final Opc btrl = new i386_Opc(_btrl, "btrl");

  /* BTS */
  public static final Opc btsw = new i386_Opc(_btsw, "btsw");
  public static final Opc btsl = new i386_Opc(_btsl, "btsl");

  /* CALL */
  public static final Opc call = new i386_Opc(_call, "call");
  public static final Opc callw = new i386_Opc(_callw, "callw");
  public static final Opc calll = new i386_Opc(_calll, "calll");
  public static final Opc callf = new i386_Opc(_callf, "callf");
  public static final Opc callfw = new i386_Opc(_callfw, "callfw");
  public static final Opc callfl = new i386_Opc(_callfl, "callfl");

  /* CBW/CWDE */
  public static final Opc cbw = new i386_Opc(_cbw, "cbw");
  public static final Opc cwde = new i386_Opc(_cwde, "cwde");

  /* CLC */
  public static final Opc clc = new i386_Opc(_clc, "clc");

  /* CLD */
  public static final Opc cld = new i386_Opc(_cld, "cld");

  /* CLI */
  public static final Opc cli = new i386_Opc(_cli, "cli");

  /* CLTS */
  public static final Opc clts = new i386_Opc(_clts, "clts");

  /* CMC */
  public static final Opc cmc = new i386_Opc(_cmc, "cmc");

  /* CMP */
  public static final Opc cmpb = new i386_Opc(_cmpb, "cmpb");
  public static final Opc cmpw = new i386_Opc(_cmpw, "cmpw");
  public static final Opc cmpl = new i386_Opc(_cmpl, "cmpl");

  /* CMPS/CMPSB/CMPSW/CMPSD */
  public static final Opc cmpsb = new i386_Opc(_cmpsb, "cmpsb");
  public static final Opc cmpsw = new i386_Opc(_cmpsw, "cmpsw");
  public static final Opc cmpsd = new i386_Opc(_cmpsd, "cmpsd");

  /* CWD/CDQ */
  public static final Opc cwd = new i386_Opc(_cwd, "cwd");
  public static final Opc cdq = new i386_Opc(_cdq, "cdq");

  /* DAA */
  public static final Opc daa = new i386_Opc(_daa, "daa");

  /* DAS */
  public static final Opc das = new i386_Opc(_das, "das");

  /* DEC */
  public static final Opc decb = new i386_Opc(_decb, "decb");
  public static final Opc decw = new i386_Opc(_decw, "decw");
  public static final Opc decl = new i386_Opc(_decl, "decl");

  /* DIV */
  public static final Opc divb = new i386_Opc(_divb, "divb");
  public static final Opc divw = new i386_Opc(_divw, "divw");
  public static final Opc divl = new i386_Opc(_divl, "divl");

  /* ENTER */
  public static final Opc enter = new i386_Opc(_enter, "enter");

  /* F2XM1 */
  public static final Opc f2xm1 = new i386_Opc(_f2xm1, "f2xm1");

  /* FABS */
  public static final Opc fabs = new i386_Opc(_fabs, "fabs");

  /* FADD/FADDP/FIADD */
  public static final Opc fadds = new i386_Opc(_fadds, "fadds");
  public static final Opc faddl = new i386_Opc(_faddl, "faddl");
  public static final Opc fadd = new i386_Opc(_fadd, "fadd");
  public static final Opc faddp = new i386_Opc(_faddp, "faddp");
  public static final Opc fiaddw = new i386_Opc(_fiaddw, "fiaddw");
  public static final Opc fiaddl = new i386_Opc(_fiaddl, "fiaddl");

  /* FBLD */
  public static final Opc fbld = new i386_Opc(_fbld, "fbld");

  /* FBSTP */
  public static final Opc fbstp = new i386_Opc(_fbstp, "fbstp");

  /* FCHS */
  public static final Opc fchs = new i386_Opc(_fchs, "fchs");

  /* FCLEX/FNCLEX */
  public static final Opc fclex = new i386_Opc(_fclex, "fclex");
  public static final Opc fnclex = new i386_Opc(_fnclex, "fnclex");

  /* FCOM/FCOMP/FCOMPP */
  public static final Opc fcoms = new i386_Opc(_fcoms, "fcoms");
  public static final Opc fcoml = new i386_Opc(_fcoml, "fcoml");
  public static final Opc fcom = new i386_Opc(_fcom, "fcom");
  public static final Opc fcomps = new i386_Opc(_fcomps, "fcomps");
  public static final Opc fcompl = new i386_Opc(_fcompl, "fcompl");
  public static final Opc fcomp = new i386_Opc(_fcomp, "fcomp");
  public static final Opc fcompp = new i386_Opc(_fcompp, "fcompp");

  /* FCOS */
  public static final Opc fcos = new i386_Opc(_fcos, "fcos");

  /* FDECSTP */
  public static final Opc fdecstp = new i386_Opc(_fdecstp, "fdecstp");
  
  /* FDIV/FDIVP/FIDIV */
  public static final Opc fdivs = new i386_Opc(_fdivs, "fdivs");
  public static final Opc fdivl = new i386_Opc(_fdivl, "fdivl");
  public static final Opc fdiv = new i386_Opc(_fdiv, "fdiv");
  public static final Opc fdivp = new i386_Opc(_fdivp, "fdivp");
  public static final Opc fidivw = new i386_Opc(_fidivw, "fidivw");
  public static final Opc fidivl = new i386_Opc(_fidivl, "fidivl");

  /* FDIVR/FDIVRP/FIDIVR */
  public static final Opc fdivrs = new i386_Opc(_fdivrs, "fdivrs");
  public static final Opc fdivrl = new i386_Opc(_fdivrl, "fdivrl");
  public static final Opc fdivr = new i386_Opc(_fdivr, "fdivr");
  public static final Opc fdivrp = new i386_Opc(_fdivrp, "fdivrp");
  public static final Opc fidivrw = new i386_Opc(_fidivrw, "fidivrw");
  public static final Opc fidivrl = new i386_Opc(_fidivrl, "fidivrl");

  /* FFREE */
  public static final Opc ffree = new i386_Opc(_ffree, "ffree");

  /* FICOM/FICOMP */
  public static final Opc ficomw = new i386_Opc(_ficomw, "ficomw");
  public static final Opc ficoml = new i386_Opc(_ficoml, "ficoml");
  public static final Opc ficompw = new i386_Opc(_ficompw, "ficompw");
  public static final Opc ficompl = new i386_Opc(_ficompl, "ficompl");
  
  /* FILD */
  public static final Opc fildw = new i386_Opc(_fildw, "fildw");
  public static final Opc fildl = new i386_Opc(_fildl, "fildl");
  public static final Opc fildll = new i386_Opc(_fildll, "fildll");

  /* FINCSTP */
  public static final Opc fincstp = new i386_Opc(_fincstp, "fincstp");

  /* FINIT/FNINIT */
  public static final Opc finit = new i386_Opc(_finit, "finit");
  public static final Opc fninit = new i386_Opc(_fninit, "fninit");

  /* FIST/FISTP */
  public static final Opc fistw = new i386_Opc(_fistw, "fistw");
  public static final Opc fistl = new i386_Opc(_fistl, "fistl");
  public static final Opc fistpw = new i386_Opc(_fistpw, "fistpw");
  public static final Opc fistpl = new i386_Opc(_fistpl, "fistpl");
  public static final Opc fistpll = new i386_Opc(_fistpll, "fistpll");

  /* FLD */
  public static final Opc flds = new i386_Opc(_flds, "flds");
  public static final Opc fldl = new i386_Opc(_fldl, "fldl");
  public static final Opc fldt = new i386_Opc(_fldt, "fldt");
  public static final Opc fld = new i386_Opc(_fld, "fld");

  /* FLD1/FLDL2T/FLDL2E/FLDPI/FLDLG2/FLDLN2/FLDZ */
  public static final Opc fld1 = new i386_Opc(_fld1, "fld1");
  public static final Opc fldl2t = new i386_Opc(_fldl2t, "fldl2t");
  public static final Opc fldl2e = new i386_Opc(_fldl2e, "fldl2e");
  public static final Opc fldpi = new i386_Opc(_fldpi, "fldpi");
  public static final Opc fldlg2 = new i386_Opc(_fldlg2, "fldlg2");
  public static final Opc fldln2 = new i386_Opc(_fldln2, "fldln2");
  public static final Opc fldz = new i386_Opc(_fldz, "fldz");

  /* FLDCW */
  public static final Opc fldcw = new i386_Opc(_fldcw, "fldcw");

  /* FLDENV */
  public static final Opc fldenv = new i386_Opc(_fldenv, "fldenv");

  /* FMUL/FMULP/FIMUL */
  public static final Opc fmuls = new i386_Opc(_fmuls, "fmuls");
  public static final Opc fmull = new i386_Opc(_fmull, "fmull");
  public static final Opc fmul = new i386_Opc(_fmul, "fmul");
  public static final Opc fmulp = new i386_Opc(_fmulp, "fmulp");
  public static final Opc fimulw = new i386_Opc(_fimulw, "fimulw");
  public static final Opc fimull = new i386_Opc(_fimull, "fimull");

  /* FNOP */
  public static final Opc fnop = new i386_Opc(_fnop, "fnop");

  /* FPATAN */
  public static final Opc fpatan = new i386_Opc(_fpatan, "fpatan");

  /* FPREM */
  public static final Opc fprem = new i386_Opc(_fprem, "fprem");

  /* FPREM1 */
  public static final Opc fprem1 = new i386_Opc(_fprem1, "fprem1");

  /* FPTAN */
  public static final Opc fptan = new i386_Opc(_fptan, "fptan");

  /* FRNDINT */
  public static final Opc frndint = new i386_Opc(_frndint, "frndint");

  /* FRSTOR */
  public static final Opc frstor = new i386_Opc(_frstor, "frstor");
  
  /* FSAVE/FNSAVE */
  public static final Opc fsave = new i386_Opc(_fsave, "fsave");
  public static final Opc fnsave = new i386_Opc(_fnsave, "fnsave");
  
  /* FSCALE */
  public static final Opc fscale = new i386_Opc(_fscale, "fscale");

  /* FSIN */
  public static final Opc fsin = new i386_Opc(_fsin, "fsin");

  /* FSINCOS */
  public static final Opc fsincos = new i386_Opc(_fsincos, "fsincos");

  /* FSQRT */
  public static final Opc fsqrt = new i386_Opc(_fsqrt, "fsqrt");

  /* FST/FSTP */
  public static final Opc fsts = new i386_Opc(_fsts, "fsts");
  public static final Opc fstl = new i386_Opc(_fstl, "fstl");
  public static final Opc fst = new i386_Opc(_fst, "fst");
  public static final Opc fstps = new i386_Opc(_fstps, "fstps");
  public static final Opc fstpl = new i386_Opc(_fstpl, "fstpl");
  public static final Opc fstpt = new i386_Opc(_fstpt, "fstpt");
  public static final Opc fstp = new i386_Opc(_fstp, "fstp");

  /* FSTCW/FNSTCW */
  public static final Opc fstcw = new i386_Opc(_fstcw, "fstcw");
  public static final Opc fnstcw = new i386_Opc(_fnstcw, "fnstcw");

  /* FSTENV/FNSTENV */
  public static final Opc fstenv = new i386_Opc(_fstenv, "fstenv");
  public static final Opc fnstenv = new i386_Opc(_fnstenv, "fnstenv");

  /* FSTSW/FNSTSW */
  public static final Opc fstsw = new i386_Opc(_fstsw, "fstsw");
  public static final Opc fnstsw = new i386_Opc(_fnstsw, "fnstsw");
  
  /* FSUB/FSUBP/FISUB */
  public static final Opc fsubs = new i386_Opc(_fsubs, "fsubs");
  public static final Opc fsubl = new i386_Opc(_fsubl, "fsubl");
  public static final Opc fsub = new i386_Opc(_fsub, "fsub");
  public static final Opc fsubp = new i386_Opc(_fsubp, "fsubp");
  public static final Opc fisubw = new i386_Opc(_fisubw, "fisubw");
  public static final Opc fisubl = new i386_Opc(_fisubl, "fisubl");

  /* FSUBR/FSUBRP/FISUBR */
  public static final Opc fsubrs = new i386_Opc(_fsubrs, "fsubrs");
  public static final Opc fsubrl = new i386_Opc(_fsubrl, "fsubrl");
  public static final Opc fsubr = new i386_Opc(_fsubr, "fsubr");
  public static final Opc fsubrp = new i386_Opc(_fsubrp, "fsubrp");
  public static final Opc fisubrw = new i386_Opc(_fisubrw, "fisubrw");
  public static final Opc fisubrl = new i386_Opc(_fisubrl, "fisubrl");

  /* FTST */
  public static final Opc ftst = new i386_Opc(_ftst, "ftst");

  /* FUCOM/FUCOMP/FUCOMPP */
  public static final Opc fucom = new i386_Opc(_fucom, "fucom");
  public static final Opc fucomp = new i386_Opc(_fucomp, "fucomp");
  public static final Opc fucompp = new i386_Opc(_fucompp, "fucompp");

  /* FXAM */
  public static final Opc fxam = new i386_Opc(_fxam, "fxam");
  
  /* FXCH */
  public static final Opc fxch = new i386_Opc(_fxch, "fxch");

  /* FXTRACT */
  public static final Opc fxtract = new i386_Opc(_fxtract, "fxtract");

  /* FYL2X */
  public static final Opc fyl2x = new i386_Opc(_fyl2x, "fyl2x");

  /* FYL2XP1 */
  public static final Opc fyl2xp1 = new i386_Opc(_fyl2xp1, "fyl2xp1");

  /* HLT */
  public static final Opc hlt = new i386_Opc(_hlt, "hlt");

  /* IDIV */
  public static final Opc idivb = new i386_Opc(_idivb, "idivb");
  public static final Opc idivw = new i386_Opc(_idivw, "idivw");
  public static final Opc idivl = new i386_Opc(_idivl, "idivl");

  /* IMUL */
  public static final Opc imulb = new i386_Opc(_imulb, "imulb");
  public static final Opc imulw = new i386_Opc(_imulw, "imulw");
  public static final Opc imull = new i386_Opc(_imull, "imull");

  /* IN */
  public static final Opc inb = new i386_Opc(_inb, "inb");
  public static final Opc inw = new i386_Opc(_inw, "inw");
  public static final Opc inl = new i386_Opc(_inl, "inl");

  /* INC */
  public static final Opc incb = new i386_Opc(_incb, "incb");
  public static final Opc incw = new i386_Opc(_incw, "incw");
  public static final Opc incl = new i386_Opc(_incl, "incl");

  /* INS/INSB/INSW/INSD */
  public static final Opc insb = new i386_Opc(_insb, "insb");
  public static final Opc insw = new i386_Opc(_insw, "insw");
  public static final Opc insd = new i386_Opc(_insd, "insd");

  /* INT/INTO */
  public static final Opc inti = new i386_Opc(_inti, "inti");
  public static final Opc into = new i386_Opc(_into, "into");

  /* IRET/IRETD */
  public static final Opc iret = new i386_Opc(_iret, "iret");
  public static final Opc iretd = new i386_Opc(_iretd, "iretd");

  /* Jcc */
  public static final Opc ja = new i386_Opc(_ja, "ja");
  public static final Opc jae = new i386_Opc(_jae, "jae");
  public static final Opc jb = new i386_Opc(_jb, "jb");
  public static final Opc jbe = new i386_Opc(_jbe, "jbe");
  public static final Opc jc = new i386_Opc(_jc, "jc");
  public static final Opc jcxe = new i386_Opc(_jcxe, "jcxe");
  public static final Opc jcxz = new i386_Opc(_jcxz, "jcxz");
  public static final Opc jecxe = new i386_Opc(_jecxe, "jecxe");
  public static final Opc jecxz = new i386_Opc(_jecxz, "jecxz");
  public static final Opc je = new i386_Opc(_je, "je");
  public static final Opc jg = new i386_Opc(_jg, "jg");
  public static final Opc jge = new i386_Opc(_jge, "jge");
  public static final Opc jl = new i386_Opc(_jl, "jl");
  public static final Opc jle = new i386_Opc(_jle, "jle");
  public static final Opc jna = new i386_Opc(_jna, "jna");
  public static final Opc jnae = new i386_Opc(_jnae, "jnae");
  public static final Opc jnb = new i386_Opc(_jnb, "jnb");
  public static final Opc jnbe = new i386_Opc(_jnbe, "jnbe");
  public static final Opc jnc = new i386_Opc(_jnc, "jnc");
  public static final Opc jne = new i386_Opc(_jne, "jne");
  public static final Opc jng = new i386_Opc(_jng, "jng");
  public static final Opc jnge = new i386_Opc(_jnge, "jnge");
  public static final Opc jnl = new i386_Opc(_jnl, "jnl");
  public static final Opc jnle = new i386_Opc(_jnle, "jnle");
  public static final Opc jno = new i386_Opc(_jno, "jno");
  public static final Opc jnp = new i386_Opc(_jnp, "jnp");
  public static final Opc jns = new i386_Opc(_jns, "jns");
  public static final Opc jnz = new i386_Opc(_jnz, "jnz");
  public static final Opc jo = new i386_Opc(_jo, "jo");
  public static final Opc jp = new i386_Opc(_jp, "jp");
  public static final Opc jpe = new i386_Opc(_jpe, "jpe");
  public static final Opc jpo = new i386_Opc(_jpo, "jpo");
  public static final Opc js = new i386_Opc(_js, "js");
  public static final Opc jz = new i386_Opc(_jz, "jz");

  /* JMP */
  public static final Opc jmp = new i386_Opc(_jmp, "jmp");
  public static final Opc jmpw = new i386_Opc(_jmpw, "jmpw");
  public static final Opc jmpl = new i386_Opc(_jmpl, "jmpl");
  public static final Opc jmpf = new i386_Opc(_jmpf, "jmpf");
  public static final Opc jmpfw = new i386_Opc(_jmpfw, "jmpfw");
  public static final Opc jmpfl = new i386_Opc(_jmpfl, "jmpfl");

  /* LAHF */
  public static final Opc lahf = new i386_Opc(_lahf, "lahf");

  /* LAR */
  public static final Opc larw = new i386_Opc(_larw, "larw");
  public static final Opc larl = new i386_Opc(_larl, "larl");

  /* LDS/LES/LFS/LGS/LSS */
  public static final Opc ldsw = new i386_Opc(_ldsw, "ldsw");
  public static final Opc ldsl = new i386_Opc(_ldsl, "ldsl");
  public static final Opc lssw = new i386_Opc(_lssw, "lssw");
  public static final Opc lssl = new i386_Opc(_lssl, "lssl");
  public static final Opc lesw = new i386_Opc(_lesw, "lesw");
  public static final Opc lesl = new i386_Opc(_lesl, "lesl");
  public static final Opc lfsw = new i386_Opc(_lfsw, "lfsw");
  public static final Opc lfsl = new i386_Opc(_lfsl, "lfsl");
  public static final Opc lgsw = new i386_Opc(_lgsw, "lgsw");
  public static final Opc lgsl = new i386_Opc(_lgsl, "lgsl");

  /* LEA */
  public static final Opc leaw = new i386_Opc(_leaw, "leaw");
  public static final Opc leal = new i386_Opc(_leal, "leal");

  /* LEAVE */
  public static final Opc leave = new i386_Opc(_leave, "leave");
  
  /* LGDT/LIDT */
  public static final Opc lgdt = new i386_Opc(_lgdt, "lgdt");
  public static final Opc lidt = new i386_Opc(_lidt, "lidt");

  /* LLDT */
  public static final Opc lldt = new i386_Opc(_lldt, "lldt");

  /* LMSW */
  public static final Opc lmsw = new i386_Opc(_lmsw, "lmsw");

  /* LOCK */
  public static final Opc lock = new i386_Opc(_lock, "lock");

  /* LODS/LODSB/LODSW/LODSD */
  public static final Opc lodsb = new i386_Opc(_lodsb, "lodsb");
  public static final Opc lodsw = new i386_Opc(_lodsw, "lodsw");
  public static final Opc lodsd = new i386_Opc(_lodsd, "lodsd");

  /* LOOP/LOOPcc */
  public static final Opc loop = new i386_Opc(_loop, "loop");
  public static final Opc loope = new i386_Opc(_loope, "loope");
  public static final Opc loopz = new i386_Opc(_loopz, "loopz");
  public static final Opc loopne = new i386_Opc(_loopne, "loopne");
  public static final Opc loopnz = new i386_Opc(_loopnz, "loopnz");
  
  /* LSL */
  public static final Opc lslw = new i386_Opc(_lslw, "lslw");
  public static final Opc lsll = new i386_Opc(_lsll, "lsll");

  /* LTR */
  public static final Opc ltr = new i386_Opc(_ltr, "ltr");

  /* MOV */
  public static final Opc movb = new i386_Opc(_movb, "movb");
  public static final Opc movw = new i386_Opc(_movw, "movw");
  public static final Opc movl = new i386_Opc(_movl, "movl");
  public static final Opc movws = new i386_Opc(_movws, "movws");
  public static final Opc movsw = new i386_Opc(_movsw, "movsw");

  /* MOVc */
  public static final Opc movlc = new i386_Opc(_movlc, "movlc");
  public static final Opc movcl = new i386_Opc(_movcl, "movcl");

  /* MOVg */
  public static final Opc movlg = new i386_Opc(_movlg, "movlg");
  public static final Opc movgl = new i386_Opc(_movgl, "movgl");

  /* MOVt */
  public static final Opc movlt = new i386_Opc(_movlt, "movlt");
  public static final Opc movtl = new i386_Opc(_movtl, "movtl");

  /* MOVSB/MOVSW/MOVSD */
  public static final Opc movsb = new i386_Opc(_movsb, "movsb");
  public static final Opc movsd = new i386_Opc(_movsd, "movsd");

  /* MOVSX */
  public static final Opc movsxbw = new i386_Opc(_movsxbw, "movsxbw");
  public static final Opc movsxbl = new i386_Opc(_movsxbl, "movsxbl");
  public static final Opc movsxwl = new i386_Opc(_movsxwl, "movsxwl");

  /* MOVZX */
  public static final Opc movzxbw = new i386_Opc(_movzxbw, "movzxbw");
  public static final Opc movzxbl = new i386_Opc(_movzxbl, "movzxbl");
  public static final Opc movzxwl = new i386_Opc(_movzxwl, "movzxwl");

  /* MUL */
  public static final Opc mulb = new i386_Opc(_mulb, "mulb");
  public static final Opc mulw = new i386_Opc(_mulw, "mulw");
  public static final Opc mull = new i386_Opc(_mull, "mull");

  /* NEG */
  public static final Opc negb = new i386_Opc(_negb, "negb");
  public static final Opc negw = new i386_Opc(_negw, "negw");
  public static final Opc negl = new i386_Opc(_negl, "negl");

  /* NOP */
  public static final Opc nop = new i386_Opc(_nop, "nop");

  /* NOT */
  public static final Opc notb = new i386_Opc(_notb, "notb");
  public static final Opc notw = new i386_Opc(_notw, "notw");
  public static final Opc notl = new i386_Opc(_notl, "notl");

  /* OR */
  public static final Opc orb = new i386_Opc(_orb, "orb");
  public static final Opc orw = new i386_Opc(_orw, "orw");
  public static final Opc orl = new i386_Opc(_orl, "orl");

  /* OUT */
  public static final Opc outb = new i386_Opc(_outb, "outb");
  public static final Opc outw = new i386_Opc(_outw, "outw");
  public static final Opc outl = new i386_Opc(_outl, "outl");

  /* OUTS/OUTSB/OUTSW/OUTSD */
  public static final Opc outsb = new i386_Opc(_outsb, "outsb");
  public static final Opc outsw = new i386_Opc(_outsw, "outsw");
  public static final Opc outsd = new i386_Opc(_outsd, "outsd");

  /* POP */
  public static final Opc popw = new i386_Opc(_popw, "popw");
  public static final Opc popl = new i386_Opc(_popl, "popl");
  public static final Opc pops = new i386_Opc(_pops, "pops");

  /* POPA/POPAD */
  public static final Opc popa = new i386_Opc(_popa, "popa");
  public static final Opc popad = new i386_Opc(_popad, "popad");

  /* POPF/POPFD */
  public static final Opc popf = new i386_Opc(_popf, "popf");
  public static final Opc popfd = new i386_Opc(_popfd, "popfd");

  /* PUSH */
  public static final Opc pushw = new i386_Opc(_pushw, "pushw");
  public static final Opc pushl = new i386_Opc(_pushl, "pushl");
  public static final Opc pushs = new i386_Opc(_pushs, "pushs");

  /* PUSHA/PUSHAD */
  public static final Opc pusha = new i386_Opc(_pusha, "pusha");
  public static final Opc pushad = new i386_Opc(_pushad, "pushad");

  /* PUSHF/PUSHFD */
  public static final Opc pushf = new i386_Opc(_pushf, "pushf");
  public static final Opc pushfd = new i386_Opc(_pushfd, "pushfd");

  /* RCL/RCR/ROL/ROR */
  public static final Opc rclb = new i386_Opc(_rclb, "rclb");
  public static final Opc rclw = new i386_Opc(_rclw, "rclw");
  public static final Opc rcll = new i386_Opc(_rcll, "rcll");
  public static final Opc rcrb = new i386_Opc(_rcrb, "rcrb");
  public static final Opc rcrw = new i386_Opc(_rcrw, "rcrw");
  public static final Opc rcrl = new i386_Opc(_rcrl, "rcrl");
  public static final Opc rolb = new i386_Opc(_rolb, "rolb");
  public static final Opc rolw = new i386_Opc(_rolw, "rolw");
  public static final Opc roll = new i386_Opc(_roll, "roll");
  public static final Opc rorb = new i386_Opc(_rorb, "rorb");
  public static final Opc rorw = new i386_Opc(_rorw, "rorw");
  public static final Opc rorl = new i386_Opc(_rorl, "rorl");

  /* REP/REPE/REPZ/REPNE/REPNZ */
  public static final Opc rep = new i386_Opc(_rep, "rep");
  public static final Opc repe = new i386_Opc(_repe, "repe");
  public static final Opc repz = new i386_Opc(_repz, "repz");
  public static final Opc repne = new i386_Opc(_repne, "repne");
  public static final Opc repnz = new i386_Opc(_repnz, "repnz");

  /* RET */
  public static final Opc ret = new i386_Opc(_ret, "ret");
  public static final Opc retf = new i386_Opc(_retf, "retf");

  /* SAHF */
  public static final Opc sahf = new i386_Opc(_sahf, "sahf");

  /* SAL/SAR/SHL/SHR */
  public static final Opc salb = new i386_Opc(_salb, "salb");
  public static final Opc salw = new i386_Opc(_salw, "salw");
  public static final Opc sall = new i386_Opc(_sall, "sall");
  public static final Opc sarb = new i386_Opc(_sarb, "sarb");
  public static final Opc sarw = new i386_Opc(_sarw, "sarw");
  public static final Opc sarl = new i386_Opc(_sarl, "sarl");
  public static final Opc shlb = new i386_Opc(_shlb, "shlb");
  public static final Opc shlw = new i386_Opc(_shlw, "shlw");
  public static final Opc shll = new i386_Opc(_shll, "shll");
  public static final Opc shrb = new i386_Opc(_shrb, "shrb");
  public static final Opc shrw = new i386_Opc(_shrw, "shrw");
  public static final Opc shrl = new i386_Opc(_shrl, "shrl");

  /* SBB */
  public static final Opc sbbb = new i386_Opc(_sbbb, "sbbb");
  public static final Opc sbbw = new i386_Opc(_sbbw, "sbbw");
  public static final Opc sbbl = new i386_Opc(_sbbl, "sbbl");

  /* SCASB/SCASW/SCASD */
  public static final Opc scasb = new i386_Opc(_scasb, "scasb");
  public static final Opc scasw = new i386_Opc(_scasw, "scasw");
  public static final Opc scasd = new i386_Opc(_scasd, "scasd");

  /* SETcc */
  public static final Opc seta = new i386_Opc(_seta, "seta");
  public static final Opc setae = new i386_Opc(_setae, "setae");
  public static final Opc setb = new i386_Opc(_setb, "setb");
  public static final Opc setbe = new i386_Opc(_setbe, "setbe");
  public static final Opc setc = new i386_Opc(_setc, "setc");
  public static final Opc sete = new i386_Opc(_sete, "sete");
  public static final Opc setg = new i386_Opc(_setg, "setg");
  public static final Opc setge = new i386_Opc(_setge, "setge");
  public static final Opc setl = new i386_Opc(_setl, "setl");
  public static final Opc setle = new i386_Opc(_setle, "setle");
  public static final Opc setna = new i386_Opc(_setna, "setna");
  public static final Opc setnae = new i386_Opc(_setnae, "setnae");
  public static final Opc setnb = new i386_Opc(_setnb, "setnb");
  public static final Opc setnbe = new i386_Opc(_setnbe, "setnbe");
  public static final Opc setnc = new i386_Opc(_setnc, "setnc");
  public static final Opc setne = new i386_Opc(_setne, "setne");
  public static final Opc setng = new i386_Opc(_setng, "setng");
  public static final Opc setnge = new i386_Opc(_setnge, "setnge");
  public static final Opc setnl = new i386_Opc(_setnl, "setnl");
  public static final Opc setnle = new i386_Opc(_setnle, "setnle");
  public static final Opc setno = new i386_Opc(_setno, "setno");
  public static final Opc setnp = new i386_Opc(_setnp, "setnp");
  public static final Opc setns = new i386_Opc(_setns, "setns");
  public static final Opc setnz = new i386_Opc(_setnz, "setnz");
  public static final Opc seto = new i386_Opc(_seto, "seto");
  public static final Opc setp = new i386_Opc(_setp, "setp");
  public static final Opc setpe = new i386_Opc(_setpe, "setpe");
  public static final Opc setpo = new i386_Opc(_setpo, "setpo");
  public static final Opc sets = new i386_Opc(_sets, "sets");
  public static final Opc setz = new i386_Opc(_setz, "setz");

  /* SGDT/SIDT */
  public static final Opc sgdt = new i386_Opc(_sgdt, "sgdt");
  public static final Opc sidt = new i386_Opc(_sidt, "sidt");

  /* SHLD */
  public static final Opc shldw = new i386_Opc(_shldw, "shldw");
  public static final Opc shldl = new i386_Opc(_shldl, "shldl");

  /* SHRD */
  public static final Opc shrdw = new i386_Opc(_shrdw, "shrdw");
  public static final Opc shrdl = new i386_Opc(_shrdl, "shrdl");

  /* SLDT */
  public static final Opc sldt = new i386_Opc(_sldt, "sldt");

  /* SMSW */
  public static final Opc smsw = new i386_Opc(_smsw, "smsw");

  /* STC */
  public static final Opc stc = new i386_Opc(_stc, "stc");

  /* STD */
  public static final Opc std = new i386_Opc(_std, "std");

  /* STI */
  public static final Opc sti = new i386_Opc(_sti, "sti");

  /* STOSB/STOSW/STOSD */
  public static final Opc stosb = new i386_Opc(_stosb, "stosb");
  public static final Opc stosw = new i386_Opc(_stosw, "stosw");
  public static final Opc stosd = new i386_Opc(_stosd, "stosd");

  /* STR */
  public static final Opc str = new i386_Opc(_str, "str");

  /* SUB */
  public static final Opc subb = new i386_Opc(_subb, "subb");
  public static final Opc subw = new i386_Opc(_subw, "subw");
  public static final Opc subl = new i386_Opc(_subl, "subl");

  /* TEST */
  public static final Opc testb = new i386_Opc(_testb, "testb");
  public static final Opc testw = new i386_Opc(_testw, "testw");
  public static final Opc testl = new i386_Opc(_testl, "testl");

  /* VERR/VERW */
  public static final Opc verr = new i386_Opc(_verr, "verr");
  public static final Opc verw = new i386_Opc(_verw, "verw");

  /* WAIT/FWAIT */
  public static final Opc fwait = new i386_Opc(_fwait, "fwait");

  /* XCHG */
  public static final Opc xchgb = new i386_Opc(_xchgb, "xchgb");
  public static final Opc xchgw = new i386_Opc(_xchgw, "xchgw");
  public static final Opc xchgl = new i386_Opc(_xchgl, "xchgl");

  /* XLATB */
  public static final Opc xlatb = new i386_Opc(_xlatb, "xlatb");

  /* XOR */
  public static final Opc xorb = new i386_Opc(_xorb, "xorb");
  public static final Opc xorw = new i386_Opc(_xorw, "xorw");
  public static final Opc xorl = new i386_Opc(_xorl, "xorl");

}

