/**
 * Copyright (c) www.bugull.com
 */
package com.loumt.spring;

/**
 * Spring中处理Url中特殊字符的类
 */

public final class UriUtil {
    private static final String URI_UNESCAPED_NONALPHANUMERIC = "-_.!~*\'()";
    private static final String URI_RESERVED = ";/?:@&=+$,#";

    private UriUtil() {
    }

    static String encodeURI(Object self, String string) {
        return encode(self, string, false);
    }

    static String encodeURIComponent(Object self, String string) {
        return encode(self, string, true);
    }

    static String decodeURI(Object self, String string) {
        return decode(self, string, false);
    }

    static String decodeURIComponent(Object self, String string) {
        return decode(self, string, true);
    }

    private static String encode(Object self, String string, boolean component) {
        if(string.isEmpty()) {
            return string;
        } else {
            int len = string.length();
            StringBuilder sb = new StringBuilder();

            for(int k = 0; k < len; ++k) {
                char C = string.charAt(k);
                if(isUnescaped(C, component)) {
                    sb.append(C);
                } else {
                    if(C >= '\udc00' && C <= '\udfff') {
                        return error(string, k);
                    }

                    int V;
                    if(C >= '\ud800' && C <= '\udbff') {
                        ++k;
                        if(k == len) {
                            return error(string, k);
                        }

                        char e = string.charAt(k);
                        if(e < '\udc00' || e > '\udfff') {
                            return error(string, k);
                        }

                        V = (C - '\ud800') * 1024 + (e - '\udc00') + 65536;
                    } else {
                        V = C;
                    }

                    try {
                        sb.append(toHexEscape(V));
                    } catch (Exception var9) {
                    }
                }
            }

            return sb.toString();
        }
    }

    private static String decode(Object self, String string, boolean component) {
        if(string.isEmpty()) {
            return string;
        } else {
            int len = string.length();
            StringBuilder sb = new StringBuilder();

            for(int k = 0; k < len; ++k) {
                char ch = string.charAt(k);
                if(ch != 37) {
                    sb.append(ch);
                } else {
                    int start = k;
                    if(k + 2 >= len) {
                        return error(string, k);
                    }

                    int B = toHexByte(string.charAt(k + 1), string.charAt(k + 2));
                    if(B < 0) {
                        return error(string, k + 1);
                    }

                    k += 2;
                    char C;
                    if((B & 128) == 0) {
                        C = (char)B;
                        if(!component && ";/?:@&=+$,#".indexOf(C) >= 0) {
                            for(int var15 = start; var15 <= k; ++var15) {
                                sb.append(string.charAt(var15));
                            }
                        } else {
                            sb.append(C);
                        }
                    } else {
                        if((B & 192) == 128) {
                            return error(string, k);
                        }

                        byte n;
                        int V;
                        int minV;
                        if((B & 32) == 0) {
                            n = 2;
                            V = B & 31;
                            minV = 128;
                        } else if((B & 16) == 0) {
                            n = 3;
                            V = B & 15;
                            minV = 2048;
                        } else if((B & 8) == 0) {
                            n = 4;
                            V = B & 7;
                            minV = 65536;
                        } else if((B & 4) == 0) {
                            n = 5;
                            V = B & 3;
                            minV = 2097152;
                        } else {
                            if((B & 2) != 0) {
                                return error(string, k);
                            }

                            n = 6;
                            V = B & 1;
                            minV = 67108864;
                        }

                        if(k + 3 * (n - 1) >= len) {
                            return error(string, k);
                        }

                        int L;
                        for(L = 1; L < n; ++L) {
                            ++k;
                            if(string.charAt(k) != 37) {
                                return error(string, k);
                            }

                            B = toHexByte(string.charAt(k + 1), string.charAt(k + 2));
                            if(B < 0 || (B & 192) != 128) {
                                return error(string, k + 1);
                            }

                            V = V << 6 | B & 63;
                            k += 2;
                        }

                        if(V < minV || V >= '\ud800' && V <= '\udfff') {
                            V = 2147483647;
                        }

                        if(V < 65536) {
                            C = (char)V;
                            if(!component && ";/?:@&=+$,#".indexOf(C) >= 0) {
                                for(L = start; L != k; ++L) {
                                    sb.append(string.charAt(L));
                                }
                            } else {
                                sb.append(C);
                            }
                        } else {
                            if(V > 1114111) {
                                return error(string, k);
                            }

                            L = (V - 65536 & 1023) + '\udc00';
                            int H = (V - 65536 >> 10 & 1023) + '\ud800';
                            sb.append((char)H);
                            sb.append((char)L);
                        }
                    }
                }
            }

            return sb.toString();
        }
    }

    private static int hexDigit(char ch) {
        char chu = Character.toUpperCase(ch);
        return chu >= 48 && chu <= 57?chu - 48:(chu >= 65 && chu <= 70?chu - 65 + 10:-1);
    }

    private static int toHexByte(char ch1, char ch2) {
        int i1 = hexDigit(ch1);
        int i2 = hexDigit(ch2);
        return i1 >= 0 && i2 >= 0?i1 << 4 | i2:-1;
    }

    private static String toHexEscape(int u0) {
        int u = u0;
        byte[] b = new byte[6];
        int len;
        if(u0 <= 127) {
            b[0] = (byte)u0;
            len = 1;
        } else {
            len = 2;

            int sb;
            for(sb = u0 >>> 11; sb != 0; sb >>>= 5) {
                ++len;
            }

            for(sb = len - 1; sb > 0; --sb) {
                b[sb] = (byte)(128 | u & 63);
                u >>>= 6;
            }

            b[0] = (byte)(~((1 << 8 - len) - 1) | u);
        }

        StringBuilder var6 = new StringBuilder();

        for(int i = 0; i < len; ++i) {
            var6.append('%');
            if((b[i] & 255) < 16) {
                var6.append('0');
            }

            var6.append(Integer.toHexString(b[i] & 255).toUpperCase());
        }

        return var6.toString();
    }

    private static String error(String string, int index) {
        return "bad.uri:"+new String[]{string, Integer.toString(index)};
    }

    private static boolean isUnescaped(char ch, boolean component) {
        return (65 > ch || ch > 90) && (97 > ch || ch > 122) && (48 > ch || ch > 57)?("-_.!~*\'()".indexOf(ch) >= 0?true:(!component?";/?:@&=+$,#".indexOf(ch) >= 0:false)):true;
    }
}

