package com.jmga.graphs.classes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jmga.graphs.R;
import com.jmga.graphs.tools.Bipartite;
import com.jmga.graphs.tools.Dijkstra;
import com.jmga.graphs.tools.FlowTable;
import com.jmga.graphs.tools.Kruskal;
import com.jmga.graphs.tools.XMLParser;
import com.jmga.graphs.tools.auxiliary.SizeView;

public class GView extends View {
	private Graph g;
	private Graph gKruskal;
	private Paint paint, auxP;
	private Paint fontPaint;
	private Path path;

	public boolean save_graph = false;
	public boolean info_table = false;
	public boolean cleangraph = false;
	public boolean table_dist = false;

	public boolean isKruskal = false;
	public boolean isBipartite = false;
	private boolean checked_kruskal = false;
	private boolean checked_bipartite = false;
	private Hashtable<Integer, Integer> subSets;

	private int viewportHeight, viewportWidth;

	final private SizeView size = new SizeView();
	private static final String[] label = { "A", "B", "C", "D", "E", "F", "G",
		"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
		"U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g",
		"h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
		"u", "v", "w", "x", "y", "z", "AA", "AB", "AC", "AD", "AE", "AF",
		"AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ",
		"AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "Aa", "Ab",
		"Ac", "Ad", "Ae", "Af", "Ag", "Ah", "Ai", "Aj", "Ak", "Al", "Am",
		"An", "Ao", "Ap", "Aq", "Ar", "As", "At", "Au", "Av", "Aw", "Ax",
		"Ay", "Az", "BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI",
		"BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT",
		"BU", "BV", "BW", "BX", "BY", "BZ", "Ba", "Bb", "Bc", "Bd", "Be",
		"Bf", "Bg", "Bh", "Bi", "Bj", "Bk", "Bl", "Bm", "Bn", "Bo", "Bp",
		"Bq", "Br", "Bs", "Bt", "Bu", "Bv", "Bw", "Bx", "By", "Bz", "CA",
		"CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL",
		"CM", "CN", "CO", "CP", "CQ", "CR", "CS", "CT", "CU", "CV", "CW",
		"CX", "CY", "CZ", "Ca", "Cb", "Cc", "Cd", "Ce", "Cf", "Cg", "Ch",
		"Ci", "Cj", "Ck", "Cl", "Cm", "Cn", "Co", "Cp", "Cq", "Cr", "Cs",
		"Ct", "Cu", "Cv", "Cw", "Cx", "Cy", "Cz", "DA", "DB", "DC", "DD",
		"DE", "DF", "DG", "DH", "DI", "DJ", "DK", "DL", "DM", "DN", "DO",
		"DP", "DQ", "DR", "DS", "DT", "DU", "DV", "DW", "DX", "DY", "DZ",
		"Da", "Db", "Dc", "Dd", "De", "Df", "Dg", "Dh", "Di", "Dj", "Dk",
		"Dl", "Dm", "Dn", "Do", "Dp", "Dq", "Dr", "Ds", "Dt", "Du", "Dv",
		"Dw", "Dx", "Dy", "Dz", "EA", "EB", "EC", "ED", "EE", "EF", "EG",
		"EH", "EI", "EJ", "EK", "EL", "EM", "EN", "EO", "EP", "EQ", "ER",
		"ES", "ET", "EU", "EV", "EW", "EX", "EY", "EZ", "Ea", "Eb", "Ec",
		"Ed", "Ee", "Ef", "Eg", "Eh", "Ei", "Ej", "Ek", "El", "Em", "En",
		"Eo", "Ep", "Eq", "Er", "Es", "Et", "Eu", "Ev", "Ew", "Ex", "Ey",
		"Ez", "FA", "FB", "FC", "FD", "FE", "FF", "FG", "FH", "FI", "FJ",
		"FK", "FL", "FM", "FN", "FO", "FP", "FQ", "FR", "FS", "FT", "FU",
		"FV", "FW", "FX", "FY", "FZ", "Fa", "Fb", "Fc", "Fd", "Fe", "Ff",
		"Fg", "Fh", "Fi", "Fj", "Fk", "Fl", "Fm", "Fn", "Fo", "Fp", "Fq",
		"Fr", "Fs", "Ft", "Fu", "Fv", "Fw", "Fx", "Fy", "Fz", "GA", "GB",
		"GC", "GD", "GE", "GF", "GG", "GH", "GI", "GJ", "GK", "GL", "GM",
		"GN", "GO", "GP", "GQ", "GR", "GS", "GT", "GU", "GV", "GW", "GX",
		"GY", "GZ", "Ga", "Gb", "Gc", "Gd", "Ge", "Gf", "Gg", "Gh", "Gi",
		"Gj", "Gk", "Gl", "Gm", "Gn", "Go", "Gp", "Gq", "Gr", "Gs", "Gt",
		"Gu", "Gv", "Gw", "Gx", "Gy", "Gz", "HA", "HB", "HC", "HD", "HE",
		"HF", "HG", "HH", "HI", "HJ", "HK", "HL", "HM", "HN", "HO", "HP",
		"HQ", "HR", "HS", "HT", "HU", "HV", "HW", "HX", "HY", "HZ", "Ha",
		"Hb", "Hc", "Hd", "He", "Hf", "Hg", "Hh", "Hi", "Hj", "Hk", "Hl",
		"Hm", "Hn", "Ho", "Hp", "Hq", "Hr", "Hs", "Ht", "Hu", "Hv", "Hw",
		"Hx", "Hy", "Hz", "IA", "IB", "IC", "ID", "IE", "IF", "IG", "IH",
		"II", "IJ", "IK", "IL", "IM", "IN", "IO", "IP", "IQ", "IR", "IS",
		"IT", "IU", "IV", "IW", "IX", "IY", "IZ", "Ia", "Ib", "Ic", "Id",
		"Ie", "If", "Ig", "Ih", "Ii", "Ij", "Ik", "Il", "Im", "In", "Io",
		"Ip", "Iq", "Ir", "Is", "It", "Iu", "Iv", "Iw", "Ix", "Iy", "Iz",
		"JA", "JB", "JC", "JD", "JE", "JF", "JG", "JH", "JI", "JJ", "JK",
		"JL", "JM", "JN", "JO", "JP", "JQ", "JR", "JS", "JT", "JU", "JV",
		"JW", "JX", "JY", "JZ", "Ja", "Jb", "Jc", "Jd", "Je", "Jf", "Jg",
		"Jh", "Ji", "Jj", "Jk", "Jl", "Jm", "Jn", "Jo", "Jp", "Jq", "Jr",
		"Js", "Jt", "Ju", "Jv", "Jw", "Jx", "Jy", "Jz", "KA", "KB", "KC",
		"KD", "KE", "KF", "KG", "KH", "KI", "KJ", "KK", "KL", "KM", "KN",
		"KO", "KP", "KQ", "KR", "KS", "KT", "KU", "KV", "KW", "KX", "KY",
		"KZ", "Ka", "Kb", "Kc", "Kd", "Ke", "Kf", "Kg", "Kh", "Ki", "Kj",
		"Kk", "Kl", "Km", "Kn", "Ko", "Kp", "Kq", "Kr", "Ks", "Kt", "Ku",
		"Kv", "Kw", "Kx", "Ky", "Kz", "LA", "LB", "LC", "LD", "LE", "LF",
		"LG", "LH", "LI", "LJ", "LK", "LL", "LM", "LN", "LO", "LP", "LQ",
		"LR", "LS", "LT", "LU", "LV", "LW", "LX", "LY", "LZ", "La", "Lb",
		"Lc", "Ld", "Le", "Lf", "Lg", "Lh", "Li", "Lj", "Lk", "Ll", "Lm",
		"Ln", "Lo", "Lp", "Lq", "Lr", "Ls", "Lt", "Lu", "Lv", "Lw", "Lx",
		"Ly", "Lz", "MA", "MB", "MC", "MD", "ME", "MF", "MG", "MH", "MI",
		"MJ", "MK", "ML", "MM", "MN", "MO", "MP", "MQ", "MR", "MS", "MT",
		"MU", "MV", "MW", "MX", "MY", "MZ", "Ma", "Mb", "Mc", "Md", "Me",
		"Mf", "Mg", "Mh", "Mi", "Mj", "Mk", "Ml", "Mm", "Mn", "Mo", "Mp",
		"Mq", "Mr", "Ms", "Mt", "Mu", "Mv", "Mw", "Mx", "My", "Mz", "NA",
		"NB", "NC", "ND", "NE", "NF", "NG", "NH", "NI", "NJ", "NK", "NL",
		"NM", "NN", "NO", "NP", "NQ", "NR", "NS", "NT", "NU", "NV", "NW",
		"NX", "NY", "NZ", "Na", "Nb", "Nc", "Nd", "Ne", "Nf", "Ng", "Nh",
		"Ni", "Nj", "Nk", "Nl", "Nm", "Nn", "No", "Np", "Nq", "Nr", "Ns",
		"Nt", "Nu", "Nv", "Nw", "Nx", "Ny", "Nz", "OA", "OB", "OC", "OD",
		"OE", "OF", "OG", "OH", "OI", "OJ", "OK", "OL", "OM", "ON", "OO",
		"OP", "OQ", "OR", "OS", "OT", "OU", "OV", "OW", "OX", "OY", "OZ",
		"Oa", "Ob", "Oc", "Od", "Oe", "Of", "Og", "Oh", "Oi", "Oj", "Ok",
		"Ol", "Om", "On", "Oo", "Op", "Oq", "Or", "Os", "Ot", "Ou", "Ov",
		"Ow", "Ox", "Oy", "Oz", "PA", "PB", "PC", "PD", "PE", "PF", "PG",
		"PH", "PI", "PJ", "PK", "PL", "PM", "PN", "PO", "PP", "PQ", "PR",
		"PS", "PT", "PU", "PV", "PW", "PX", "PY", "PZ", "Pa", "Pb", "Pc",
		"Pd", "Pe", "Pf", "Pg", "Ph", "Pi", "Pj", "Pk", "Pl", "Pm", "Pn",
		"Po", "Pp", "Pq", "Pr", "Ps", "Pt", "Pu", "Pv", "Pw", "Px", "Py",
		"Pz", "QA", "QB", "QC", "QD", "QE", "QF", "QG", "QH", "QI", "QJ",
		"QK", "QL", "QM", "QN", "QO", "QP", "QQ", "QR", "QS", "QT", "QU",
		"QV", "QW", "QX", "QY", "QZ", "Qa", "Qb", "Qc", "Qd", "Qe", "Qf",
		"Qg", "Qh", "Qi", "Qj", "Qk", "Ql", "Qm", "Qn", "Qo", "Qp", "Qq",
		"Qr", "Qs", "Qt", "Qu", "Qv", "Qw", "Qx", "Qy", "Qz", "RA", "RB",
		"RC", "RD", "RE", "RF", "RG", "RH", "RI", "RJ", "RK", "RL", "RM",
		"RN", "RO", "RP", "RQ", "RR", "RS", "RT", "RU", "RV", "RW", "RX",
		"RY", "RZ", "Ra", "Rb", "Rc", "Rd", "Re", "Rf", "Rg", "Rh", "Ri",
		"Rj", "Rk", "Rl", "Rm", "Rn", "Ro", "Rp", "Rq", "Rr", "Rs", "Rt",
		"Ru", "Rv", "Rw", "Rx", "Ry", "Rz", "SA", "SB", "SC", "SD", "SE",
		"SF", "SG", "SH", "SI", "SJ", "SK", "SL", "SM", "SN", "SO", "SP",
		"SQ", "SR", "SS", "ST", "SU", "SV", "SW", "SX", "SY", "SZ", "Sa",
		"Sb", "Sc", "Sd", "Se", "Sf", "Sg", "Sh", "Si", "Sj", "Sk", "Sl",
		"Sm", "Sn", "So", "Sp", "Sq", "Sr", "Ss", "St", "Su", "Sv", "Sw",
		"Sx", "Sy", "Sz", "TA", "TB", "TC", "TD", "TE", "TF", "TG", "TH",
		"TI", "TJ", "TK", "TL", "TM", "TN", "TO", "TP", "TQ", "TR", "TS",
		"TT", "TU", "TV", "TW", "TX", "TY", "TZ", "Ta", "Tb", "Tc", "Td",
		"Te", "Tf", "Tg", "Th", "Ti", "Tj", "Tk", "Tl", "Tm", "Tn", "To",
		"Tp", "Tq", "Tr", "Ts", "Tt", "Tu", "Tv", "Tw", "Tx", "Ty", "Tz",
		"UA", "UB", "UC", "UD", "UE", "UF", "UG", "UH", "UI", "UJ", "UK",
		"UL", "UM", "UN", "UO", "UP", "UQ", "UR", "US", "UT", "UU", "UV",
		"UW", "UX", "UY", "UZ", "Ua", "Ub", "Uc", "Ud", "Ue", "Uf", "Ug",
		"Uh", "Ui", "Uj", "Uk", "Ul", "Um", "Un", "Uo", "Up", "Uq", "Ur",
		"Us", "Ut", "Uu", "Uv", "Uw", "Ux", "Uy", "Uz", "VA", "VB", "VC",
		"VD", "VE", "VF", "VG", "VH", "VI", "VJ", "VK", "VL", "VM", "VN",
		"VO", "VP", "VQ", "VR", "VS", "VT", "VU", "VV", "VW", "VX", "VY",
		"VZ", "Va", "Vb", "Vc", "Vd", "Ve", "Vf", "Vg", "Vh", "Vi", "Vj",
		"Vk", "Vl", "Vm", "Vn", "Vo", "Vp", "Vq", "Vr", "Vs", "Vt", "Vu",
		"Vv", "Vw", "Vx", "Vy", "Vz", "WA", "WB", "WC", "WD", "WE", "WF",
		"WG", "WH", "WI", "WJ", "WK", "WL", "WM", "WN", "WO", "WP", "WQ",
		"WR", "WS", "WT", "WU", "WV", "WW", "WX", "WY", "WZ", "Wa", "Wb",
		"Wc", "Wd", "We", "Wf", "Wg", "Wh", "Wi", "Wj", "Wk", "Wl", "Wm",
		"Wn", "Wo", "Wp", "Wq", "Wr", "Ws", "Wt", "Wu", "Wv", "Ww", "Wx",
		"Wy", "Wz", "XA", "XB", "XC", "XD", "XE", "XF", "XG", "XH", "XI",
		"XJ", "XK", "XL", "XM", "XN", "XO", "XP", "XQ", "XR", "XS", "XT",
		"XU", "XV", "XW", "XX", "XY", "XZ", "Xa", "Xb", "Xc", "Xd", "Xe",
		"Xf", "Xg", "Xh", "Xi", "Xj", "Xk", "Xl", "Xm", "Xn", "Xo", "Xp",
		"Xq", "Xr", "Xs", "Xt", "Xu", "Xv", "Xw", "Xx", "Xy", "Xz", "YA",
		"YB", "YC", "YD", "YE", "YF", "YG", "YH", "YI", "YJ", "YK", "YL",
		"YM", "YN", "YO", "YP", "YQ", "YR", "YS", "YT", "YU", "YV", "YW",
		"YX", "YY", "YZ", "Ya", "Yb", "Yc", "Yd", "Ye", "Yf", "Yg", "Yh",
		"Yi", "Yj", "Yk", "Yl", "Ym", "Yn", "Yo", "Yp", "Yq", "Yr", "Ys",
		"Yt", "Yu", "Yv", "Yw", "Yx", "Yy", "Yz", "ZA", "ZB", "ZC", "ZD",
		"ZE", "ZF", "ZG", "ZH", "ZI", "ZJ", "ZK", "ZL", "ZM", "ZN", "ZO",
		"ZP", "ZQ", "ZR", "ZS", "ZT", "ZU", "ZV", "ZW", "ZX", "ZY", "ZZ",
		"Za", "Zb", "Zc", "Zd", "Ze", "Zf", "Zg", "Zh", "Zi", "Zj", "Zk",
		"Zl", "Zm", "Zn", "Zo", "Zp", "Zq", "Zr", "Zs", "Zt", "Zu", "Zv",
		"Zw", "Zx", "Zy", "Zz", "Aa", "Ab", "Ac", "Ad", "Ae", "Af", "Ag",
		"Ah", "Ai", "Aj", "Ak", "Al", "Am", "An", "Ao", "Ap", "Aq", "Ar",
		"As", "At", "Au", "Av", "Aw", "Ax", "Ay", "Az", "Ba", "Bb", "Bc",
		"Bd", "Be", "Bf", "Bg", "Bh", "Bi", "Bj", "Bk", "Bl", "Bm", "Bn",
		"Bo", "Bp", "Bq", "Br", "Bs", "Bt", "Bu", "Bv", "Bw", "Bx", "By",
		"Bz", "Ca", "Cb", "Cc", "Cd", "Ce", "Cf", "Cg", "Ch", "Ci", "Cj",
		"Ck", "Cl", "Cm", "Cn", "Co", "Cp", "Cq", "Cr", "Cs", "Ct", "Cu",
		"Cv", "Cw", "Cx", "Cy", "Cz", "Da", "Db", "Dc", "Dd", "De", "Df",
		"Dg", "Dh", "Di", "Dj", "Dk", "Dl", "Dm", "Dn", "Do", "Dp", "Dq",
		"Dr", "Ds", "Dt", "Du", "Dv", "Dw", "Dx", "Dy", "Dz", "Ea", "Eb",
		"Ec", "Ed", "Ee", "Ef", "Eg", "Eh", "Ei", "Ej", "Ek", "El", "Em",
		"En", "Eo", "Ep", "Eq", "Er", "Es", "Et", "Eu", "Ev", "Ew", "Ex",
		"Ey", "Ez", "Fa", "Fb", "Fc", "Fd", "Fe", "Ff", "Fg", "Fh", "Fi",
		"Fj", "Fk", "Fl", "Fm", "Fn", "Fo", "Fp", "Fq", "Fr", "Fs", "Ft",
		"Fu", "Fv", "Fw", "Fx", "Fy", "Fz", "Ga", "Gb", "Gc", "Gd", "Ge",
		"Gf", "Gg", "Gh", "Gi", "Gj", "Gk", "Gl", "Gm", "Gn", "Go", "Gp",
		"Gq", "Gr", "Gs", "Gt", "Gu", "Gv", "Gw", "Gx", "Gy", "Gz", "Ha",
		"Hb", "Hc", "Hd", "He", "Hf", "Hg", "Hh", "Hi", "Hj", "Hk", "Hl",
		"Hm", "Hn", "Ho", "Hp", "Hq", "Hr", "Hs", "Ht", "Hu", "Hv", "Hw",
		"Hx", "Hy", "Hz", "Ia", "Ib", "Ic", "Id", "Ie", "If", "Ig", "Ih",
		"Ii", "Ij", "Ik", "Il", "Im", "In", "Io", "Ip", "Iq", "Ir", "Is",
		"It", "Iu", "Iv", "Iw", "Ix", "Iy", "Iz", "Ja", "Jb", "Jc", "Jd",
		"Je", "Jf", "Jg", "Jh", "Ji", "Jj", "Jk", "Jl", "Jm", "Jn", "Jo",
		"Jp", "Jq", "Jr", "Js", "Jt", "Ju", "Jv", "Jw", "Jx", "Jy", "Jz",
		"Ka", "Kb", "Kc", "Kd", "Ke", "Kf", "Kg", "Kh", "Ki", "Kj", "Kk",
		"Kl", "Km", "Kn", "Ko", "Kp", "Kq", "Kr", "Ks", "Kt", "Ku", "Kv",
		"Kw", "Kx", "Ky", "Kz", "La", "Lb", "Lc", "Ld", "Le", "Lf", "Lg",
		"Lh", "Li", "Lj", "Lk", "Ll", "Lm", "Ln", "Lo", "Lp", "Lq", "Lr",
		"Ls", "Lt", "Lu", "Lv", "Lw", "Lx", "Ly", "Lz", "Ma", "Mb", "Mc",
		"Md", "Me", "Mf", "Mg", "Mh", "Mi", "Mj", "Mk", "Ml", "Mm", "Mn",
		"Mo", "Mp", "Mq", "Mr", "Ms", "Mt", "Mu", "Mv", "Mw", "Mx", "My",
		"Mz", "Na", "Nb", "Nc", "Nd", "Ne", "Nf", "Ng", "Nh", "Ni", "Nj",
		"Nk", "Nl", "Nm", "Nn", "No", "Np", "Nq", "Nr", "Ns", "Nt", "Nu",
		"Nv", "Nw", "Nx", "Ny", "Nz", "Oa", "Ob", "Oc", "Od", "Oe", "Of",
		"Og", "Oh", "Oi", "Oj", "Ok", "Ol", "Om", "On", "Oo", "Op", "Oq",
		"Or", "Os", "Ot", "Ou", "Ov", "Ow", "Ox", "Oy", "Oz", "Pa", "Pb",
		"Pc", "Pd", "Pe", "Pf", "Pg", "Ph", "Pi", "Pj", "Pk", "Pl", "Pm",
		"Pn", "Po", "Pp", "Pq", "Pr", "Ps", "Pt", "Pu", "Pv", "Pw", "Px",
		"Py", "Pz", "Qa", "Qb", "Qc", "Qd", "Qe", "Qf", "Qg", "Qh", "Qi",
		"Qj", "Qk", "Ql", "Qm", "Qn", "Qo", "Qp", "Qq", "Qr", "Qs", "Qt",
		"Qu", "Qv", "Qw", "Qx", "Qy", "Qz", "Ra", "Rb", "Rc", "Rd", "Re",
		"Rf", "Rg", "Rh", "Ri", "Rj", "Rk", "Rl", "Rm", "Rn", "Ro", "Rp",
		"Rq", "Rr", "Rs", "Rt", "Ru", "Rv", "Rw", "Rx", "Ry", "Rz", "Sa",
		"Sb", "Sc", "Sd", "Se", "Sf", "Sg", "Sh", "Si", "Sj", "Sk", "Sl",
		"Sm", "Sn", "So", "Sp", "Sq", "Sr", "Ss", "St", "Su", "Sv", "Sw",
		"Sx", "Sy", "Sz", "Ta", "Tb", "Tc", "Td", "Te", "Tf", "Tg", "Th",
		"Ti", "Tj", "Tk", "Tl", "Tm", "Tn", "To", "Tp", "Tq", "Tr", "Ts",
		"Tt", "Tu", "Tv", "Tw", "Tx", "Ty", "Tz", "Ua", "Ub", "Uc", "Ud",
		"Ue", "Uf", "Ug", "Uh", "Ui", "Uj", "Uk", "Ul", "Um", "Un", "Uo",
		"Up", "Uq", "Ur", "Us", "Ut", "Uu", "Uv", "Uw", "Ux", "Uy", "Uz",
		"Va", "Vb", "Vc", "Vd", "Ve", "Vf", "Vg", "Vh", "Vi", "Vj", "Vk",
		"Vl", "Vm", "Vn", "Vo", "Vp", "Vq", "Vr", "Vs", "Vt", "Vu", "Vv",
		"Vw", "Vx", "Vy", "Vz", "Wa", "Wb", "Wc", "Wd", "We", "Wf", "Wg",
		"Wh", "Wi", "Wj", "Wk", "Wl", "Wm", "Wn", "Wo", "Wp", "Wq", "Wr",
		"Ws", "Wt", "Wu", "Wv", "Ww", "Wx", "Wy", "Wz", "Xa", "Xb", "Xc",
		"Xd", "Xe", "Xf", "Xg", "Xh", "Xi", "Xj", "Xk", "Xl", "Xm", "Xn",
		"Xo", "Xp", "Xq", "Xr", "Xs", "Xt", "Xu", "Xv", "Xw", "Xx", "Xy",
		"Xz", "Ya", "Yb", "Yc", "Yd", "Ye", "Yf", "Yg", "Yh", "Yi", "Yj",
		"Yk", "Yl", "Ym", "Yn", "Yo", "Yp", "Yq", "Yr", "Ys", "Yt", "Yu",
		"Yv", "Yw", "Yx", "Yy", "Yz", "Za", "Zb", "Zc", "Zd", "Ze", "Zf",
		"Zg", "Zh", "Zi", "Zj", "Zk", "Zl", "Zm", "Zn", "Zo", "Zp", "Zq",
		"Zr", "Zs", "Zt", "Zu", "Zv", "Zw", "Zx", "Zy", "Zz" };

	public static String getLabel(int i) {

	return label[i];
}
	public void setMenuStateChecked(boolean ck, boolean cb) {
		checked_kruskal = ck;
		checked_bipartite = cb;
	}
	
	public int getViewportHeight() {
		return viewportHeight;
	}

	public int getViewportWidth() {
		return viewportWidth;
	}

	public void setViewportHeight(int Y) {
		viewportHeight = Y;
	}

	public void setViewportWidth(int X) {
		viewportWidth = X;
	}

	private float density;

	public float getDensity() {
		return density;
	}

	Arrow aux;

	public GView(Context context) {
		super(context);
		init();
	}

	public GView(Context context, float density_) {
		super(context);

		init();
	}

	public GView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public GView(Context context, AttributeSet attrs, int params) {
		super(context, attrs, params);

		init();
	}

	private void init() {

		density = getResources().getDisplayMetrics().density;

		g = new Graph();
		gKruskal = new Graph();
		subSets = new Hashtable<Integer, Integer>();

		paint = new Paint();
		paint.setStrokeWidth(4f);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setAntiAlias(true);

		auxP = new Paint();
		auxP.setStrokeWidth(10f);
		auxP.setStyle(Paint.Style.STROKE);
		auxP.setStrokeJoin(Paint.Join.ROUND);
		auxP.setColor(Color.BLACK);
		auxP.setAntiAlias(true);

		fontPaint = new Paint();
		fontPaint.setTextAlign(Align.CENTER);
		fontPaint.setTextSize(20);

	}

	public boolean graphToXML(String storage, String file_name) {
		boolean task = false;
		g.update();
		XMLParser p = new XMLParser(storage, this);
		try {
			p.saveGraph(g, "/" + file_name + ".graph");
			task = true;
		} catch (Exception e) {
			task = false;
			e.printStackTrace();
		}
		return task;
	}

	public boolean isXMLGraph(String complete_path) {
		return XMLParser.isGraph(complete_path);
	}

	public boolean xmlToGraph(String storage, String xml) {
		boolean task = true;
		if (viewportHeight == 0 || viewportWidth == 0) {
			viewportHeight = (int) (50 * density + 0.5f);
			viewportWidth = (int) (50 * density + 0.5f);
			paint.setStrokeWidth(0);
			fontPaint.setTextSize(0);

		}
		XMLParser xmlp = new XMLParser(storage, xml, this);
		try {
			g = xmlp.parseGraph(g);
		} catch (Exception e) {
			e.printStackTrace();
			task = false;
		}
		return task;
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);

		for (int i = 0; i < g.getArrows().size(); i++) {
			Arrow a = g.getArrows().get(i);
			paint.setColor(g.getArrows().get(i).color);
			canvas.drawLine(a.start[0], a.start[1], a.stop[0], a.stop[1], paint);
			if (a.getWeight() > 0) {
				path = new Path();
				path.moveTo(a.start[0], a.start[1]);
				path.lineTo(a.stop[0], a.stop[1]);
				canvas.drawTextOnPath(a.getWeightS(), path, 0, 30, fontPaint);

				path = new Path();
				path.moveTo(a.stop[0], a.stop[1]);
				path.lineTo(a.start[0], a.start[1]);
				canvas.drawTextOnPath(a.getWeightS(), path, 0, 30, fontPaint);

			}

		}

		if (aux != null) {
			canvas.drawLine(aux.start[0], aux.start[1], aux.stop[0],
					aux.stop[1], auxP);
		}

		for (int ns : g.getNombres()) {
			Node n = g.getVertex().get(ns);
			n.draw(canvas);
			canvas.drawText(label[n.getId()], n.getCenterX(), n.getCenterY()
					- n.radius - 20, fontPaint);
		}

	}

	public Node checkBounds(int x, int y) {
		Iterator<Integer> ids = g.getNombres().iterator();
		while(ids.hasNext()){
			int id = ids.next();
			Node n = new Node();
			n = g.getVertex().get(id);
			if (n != null && !(n.getId()==-1)) {
				if (n.getBounds().left < x && n.getBounds().right > x
						&& n.getBounds().top < y && n.getBounds().bottom > y) {
					return n;
				}
			}
		}
		return null;
	}

	public void Kruskal() {
		restore();
		if (g.getArrows().size() >= g.getNombres().size() - 1) {
			for (int i = 0; i < g.getArrows().size(); i++) {
				for (int j = 0; j < gKruskal.getArrows().size(); j++) {
					Arrow a = g.getArrows().get(i);
					Arrow k = gKruskal.getArrows().get(j);
					if (a.getIdi()==k.getIdi()
							&& a.getIdf()==k.getIdf()) {
						a.color = Color.BLUE;
					} else if (a.getIdi()==k.getIdf()
							&& a.getIdf()==k.getIdi()) {
						a.color = Color.BLUE;
					}
				}
			}
		}
	}

	public boolean bipartite(boolean print) {
		boolean printBipatite = false;
		Bipartite b = new Bipartite(g);
		try {
			printBipatite = b.execute(true);
		} catch (Exception e) {
			printBipatite = false;
			e.printStackTrace();
		}
		if (printBipatite && print) {
			subSets = b.getSubSet();
			Enumeration<Integer> keys = subSets.keys();
			while (keys.hasMoreElements()) {
				int key = (int) keys.nextElement();
				g.setColorOfNode(key, (subSets.get(key) == 1) ? Color.YELLOW
						: Color.GREEN);
			}
		} else {
			subSets = new Hashtable<Integer, Integer>();
			initializingNodesColor();
		}

		return printBipatite;
	}

	public int connectedComponents() {
		int cc = 0;
		Bipartite b = new Bipartite(g);
		try {
			b.execute(false);
			cc = b.getConnectedComponents();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cc;
	}

	public Hashtable<String, String> getTableInfo() {
		Hashtable<String, String> info = new Hashtable<String, String>();

		info.put("|V|", Integer.toString(g.getNombres().size()));
		info.put("|A|", Integer.toString(g.getArrows().size()));
		info.put("Bipartite", (bipartite(false) ? "Si" : "No"));
		info.put("Components", Integer.toString(connectedComponents()));
		info.put("Sum", Integer.toString(g.getTotalWeight()));
		int degree = g.isRegular();
		info.put("Regular", (degree > 0) ? "Si, regular de grado " + degree
				: "No");
		info.put("Sequence", "{" + arrayParseString(g.getSequenceDegrees())
				+ "}");

		return info;
	}

	private String arrayParseString(String[] array) {
		StringBuilder builder = new StringBuilder();
		for (String s : array) {
			builder.append(s + ",");
		}
		return builder.toString().substring(0,
				builder.toString().lastIndexOf(","));
	}

	public void initializingNodesColor() {
		g.colorRestorationNodes();
	}

	public Graph aplicarKruskal(Graph g) {
		return Kruskal.aplicarKruskal(g);
	}

	public void addNode(int x, int y) {
		g.addNode(x, y, viewportWidth, viewportHeight, density);
	}

	public void addNode(Node n) {
		g.addNode(n.getCenterX(), n.getCenterY(), viewportWidth,
				viewportHeight, density);
	}

	public void deleteNode(Node n) {
		if (n != null) {
			g.deleteNode(n.getId());
		}
	}

	public void addArrow(Node n1, Node n2) {
		if (n1 != null && n2 != null) {
			g.addLink(n1.getId(), n2.getId(), 1);
		}
	}

	public void deleteArrow(Node n1, Node n2) {
		g.deleteLink(n1.getId(), n2.getId());
	}

	public void changeWeight(Node n1, Node n2, int weight) {
		g.changeWeight(n1.getId(), n2.getId(), weight);
	}

	public void addAux(Node n, int x, int y) {
		aux = new Arrow(n.getCenterX(), n.getCenterY(), x, y);
	}

	public void updateAux(int x, int y) {
		aux.stop[0] = x;
		aux.stop[1] = y;
	}

	public void deleteAux() {
		aux = null;
	}

	public void setPosition(int x, int y, Node n) {
		n.setPos(x, y, viewportWidth, viewportHeight);
	}

	public void update() {
		g.update();

		if (g.getNombres().size() > 0)
			save_graph = info_table = table_dist = cleangraph = true;
		else
			save_graph = info_table = table_dist = cleangraph = false;

		if (g.getArrows().size() >= g.getNombres().size() - 1
				&& g.getNombres().size() > 0 && g.getArrows().size() > 0) {

			isKruskal = true;
			if (checked_kruskal) {
				gKruskal = aplicarKruskal(g);
				Kruskal();
			}

		} else
			isKruskal = false;

		if (g.getNombres().size() > 0 && g.getArrows().size() > 0) {
			isBipartite = true;
			if (checked_bipartite)
				bipartite(true);
		} else {
			isBipartite = false;
			initializingNodesColor();
		}

		invalidate();
	}

	public void clear() {
		g = new Graph();
		gKruskal = new Graph();
		isKruskal = isBipartite = cleangraph = table_dist = save_graph = info_table = false;
		invalidate();
	}

	public void restore() {
		for (int i = 0; i < g.getArrows().size(); i++) {
			g.getArrows().get(i).color = Color.BLACK;
		}
	}

	public FlowTable dijkstra(Context context) {
		Dijkstra d = new Dijkstra(g);
		d.dijkstra(g);
		return d.getTableDistance(context);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewportWidth = w;
		viewportHeight = h;
	}
	
	public void resizeGraph(SizeView s){
		Log.d("PKJ",String.valueOf(s.getNew_percent()));
		if(!(s.getNew_percent()==100)){
		Enumeration<Node> nodes = g.getVertex().elements();
		while(nodes.hasMoreElements()){
			Node node = new Node();
			node = nodes.nextElement();
			float x = node.getPosX(), xo = 0;
			float y = node.getPosY(), yo = 0;
			xo = x * (float)s.getNew_width() / (float)s.getOld_width();
			yo = y * (float)s.getNew_height() / (float)s.getOld_height();
			node.setPosF(xo, yo, viewportWidth, viewportHeight);
		}
		boolean load = false;
        // [0]:x  [1]:y
		float[] displacement = new float[2];
        float[] max = new float[2];
        float[] min = new float[2];
        max[0] = max[1] = min[0] = min[1] = 0; 
        Iterator<Integer> keys = g.getNombres().iterator();
        while(keys.hasNext()){
        	Integer key = keys.next();
			if(!load){
		        max[0] = min[0] = g.getVertex().get(key).getPosX();
		        max[1] = min[1] = g.getVertex().get(key).getPosY(); 
		        load = true;
			}else{
				if(g.getVertex().get(key).getPosX() > max[0])
					max[0] = g.getVertex().get(key).getPosX();
				if(g.getVertex().get(key).getPosX() < min[0])
					min[0] = g.getVertex().get(key).getPosX();
				if(g.getVertex().get(key).getPosY() > max[1])
					max[1] = g.getVertex().get(key).getPosY();
				if(g.getVertex().get(key).getPosY() < min[1])
					min[1] = g.getVertex().get(key).getPosY();
			}
        }
        // [0]:width  [1]:height
        float[] tam = new float[2];
        for(int i=0; i<2; i++){
        	tam[i] = max[i] + min[i];
        	displacement[i] = (1 - tam[i])/2;
        }
		nodes = g.getVertex().elements();
		while(nodes.hasMoreElements()){
			Node node = new Node();
			node = nodes.nextElement();
			float x = node.getPosX() + displacement[0];
			float y = node.getPosY() + displacement[1];
			node.setPosF(x, y, viewportWidth, viewportHeight);
		}}
	}
	public void changeRadius(SizeView size) {
		// TODO Auto-generated method stub
		for(int n : g.getNombres()){
			g.getVertex().get(n).updateRadius(size.getNew_percent_vertex());
		}
		update();
		invalidate();
	}
}
