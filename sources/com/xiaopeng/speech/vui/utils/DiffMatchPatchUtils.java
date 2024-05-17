package com.xiaopeng.speech.vui.utils;

import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.bumptech.glide.load.Key;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.text.Typography;
/* loaded from: classes.dex */
public class DiffMatchPatchUtils {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public float Diff_Timeout = 1.0f;
    public short Diff_EditCost = 4;
    public float Match_Threshold = 0.5f;
    public int Match_Distance = 1000;
    public float Patch_DeleteThreshold = 0.5f;
    public short Patch_Margin = 4;
    private short Match_MaxBits = 32;
    private Pattern BLANKLINEEND = Pattern.compile("\\n\\r?\\n\\Z", 32);
    private Pattern BLANKLINESTART = Pattern.compile("\\A\\r?\\n\\r?\\n", 32);

    /* loaded from: classes.dex */
    public enum Operation {
        DELETE,
        INSERT,
        EQUAL
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class LinesToCharsResult {
        protected String chars1;
        protected String chars2;
        protected List<String> lineArray;

        protected LinesToCharsResult(String chars1, String chars2, List<String> lineArray) {
            this.chars1 = chars1;
            this.chars2 = chars2;
            this.lineArray = lineArray;
        }
    }

    public LinkedList<Diff> diff_main(String text1, String text2) {
        return diff_main(text1, text2, true);
    }

    public LinkedList<Diff> diff_main(String text1, String text2, boolean checklines) {
        long deadline;
        if (this.Diff_Timeout <= 0.0f) {
            deadline = LongCompanionObject.MAX_VALUE;
        } else {
            long deadline2 = System.currentTimeMillis();
            deadline = deadline2 + (this.Diff_Timeout * 1000.0f);
        }
        return diff_main(text1, text2, checklines, deadline);
    }

    private LinkedList<Diff> diff_main(String text1, String text2, boolean checklines, long deadline) {
        if (text1 == null || text2 == null) {
            throw new IllegalArgumentException("Null inputs. (diff_main)");
        }
        if (text1.equals(text2)) {
            LinkedList<Diff> diffs = new LinkedList<>();
            if (text1.length() != 0) {
                diffs.add(new Diff(Operation.EQUAL, text1));
            }
            return diffs;
        }
        int commonlength = diff_commonPrefix(text1, text2);
        String commonprefix = text1.substring(0, commonlength);
        String text12 = text1.substring(commonlength);
        String text22 = text2.substring(commonlength);
        int commonlength2 = diff_commonSuffix(text12, text22);
        String commonsuffix = text12.substring(text12.length() - commonlength2);
        LinkedList<Diff> diffs2 = diff_compute(text12.substring(0, text12.length() - commonlength2), text22.substring(0, text22.length() - commonlength2), checklines, deadline);
        if (commonprefix.length() != 0) {
            diffs2.addFirst(new Diff(Operation.EQUAL, commonprefix));
        }
        if (commonsuffix.length() != 0) {
            diffs2.addLast(new Diff(Operation.EQUAL, commonsuffix));
        }
        diff_cleanupMerge(diffs2);
        return diffs2;
    }

    private LinkedList<Diff> diff_compute(String text1, String text2, boolean checklines, long deadline) {
        LinkedList<Diff> diffs = new LinkedList<>();
        if (text1.length() == 0) {
            diffs.add(new Diff(Operation.INSERT, text2));
            return diffs;
        } else if (text2.length() == 0) {
            diffs.add(new Diff(Operation.DELETE, text1));
            return diffs;
        } else {
            String longtext = text1.length() > text2.length() ? text1 : text2;
            String shorttext = text1.length() > text2.length() ? text2 : text1;
            int i = longtext.indexOf(shorttext);
            if (i != -1) {
                Operation op = text1.length() > text2.length() ? Operation.DELETE : Operation.INSERT;
                diffs.add(new Diff(op, longtext.substring(0, i)));
                diffs.add(new Diff(Operation.EQUAL, shorttext));
                diffs.add(new Diff(op, longtext.substring(shorttext.length() + i)));
                return diffs;
            } else if (shorttext.length() == 1) {
                diffs.add(new Diff(Operation.DELETE, text1));
                diffs.add(new Diff(Operation.INSERT, text2));
                return diffs;
            } else {
                String[] hm = diff_halfMatch(text1, text2);
                if (hm == null) {
                    if (checklines && text1.length() > 100 && text2.length() > 100) {
                        return diff_lineMode(text1, text2, deadline);
                    }
                    return diff_bisect(text1, text2, deadline);
                }
                String text1_a = hm[0];
                String text1_b = hm[1];
                String text2_a = hm[2];
                String text2_b = hm[3];
                String mid_common = hm[4];
                LinkedList<Diff> diffs_a = diff_main(text1_a, text2_a, checklines, deadline);
                LinkedList<Diff> diffs_b = diff_main(text1_b, text2_b, checklines, deadline);
                diffs_a.add(new Diff(Operation.EQUAL, mid_common));
                diffs_a.addAll(diffs_b);
                return diffs_a;
            }
        }
    }

    private LinkedList<Diff> diff_lineMode(String text1, String text2, long deadline) {
        LinesToCharsResult a = diff_linesToChars(text1, text2);
        String text12 = a.chars1;
        String text22 = a.chars2;
        List<String> linearray = a.lineArray;
        LinkedList<Diff> diffs = diff_main(text12, text22, false, deadline);
        diff_charsToLines(diffs, linearray);
        diff_cleanupSemantic(diffs);
        diffs.add(new Diff(Operation.EQUAL, ""));
        ListIterator<Diff> pointer = diffs.listIterator();
        int count_insert = 0;
        String text_delete = "";
        String text_insert = "";
        int count_delete = 0;
        for (Diff thisDiff = pointer.next(); thisDiff != null; thisDiff = pointer.hasNext() ? pointer.next() : null) {
            int i = AnonymousClass1.$SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation[thisDiff.operation.ordinal()];
            if (i == 1) {
                count_insert++;
                text_insert = text_insert + thisDiff.text;
            } else if (i == 2) {
                count_delete++;
                text_delete = text_delete + thisDiff.text;
            } else if (i == 3) {
                if (count_delete >= 1 && count_insert >= 1) {
                    pointer.previous();
                    for (int j = 0; j < count_delete + count_insert; j++) {
                        pointer.previous();
                        pointer.remove();
                    }
                    Iterator<Diff> it = diff_main(text_delete, text_insert, false, deadline).iterator();
                    while (it.hasNext()) {
                        Diff subDiff = it.next();
                        pointer.add(subDiff);
                    }
                }
                text_delete = "";
                text_insert = "";
                count_insert = 0;
                count_delete = 0;
            }
        }
        diffs.removeLast();
        return diffs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.speech.vui.utils.DiffMatchPatchUtils$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation = new int[Operation.values().length];

        static {
            try {
                $SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation[Operation.INSERT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation[Operation.DELETE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation[Operation.EQUAL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:16:0x0057 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected java.util.LinkedList<com.xiaopeng.speech.vui.utils.DiffMatchPatchUtils.Diff> diff_bisect(java.lang.String r31, java.lang.String r32, long r33) {
        /*
            Method dump skipped, instructions count: 464
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.utils.DiffMatchPatchUtils.diff_bisect(java.lang.String, java.lang.String, long):java.util.LinkedList");
    }

    private LinkedList<Diff> diff_bisectSplit(String text1, String text2, int x, int y, long deadline) {
        String text1a = text1.substring(0, x);
        String text2a = text2.substring(0, y);
        String text1b = text1.substring(x);
        String text2b = text2.substring(y);
        LinkedList<Diff> diffs = diff_main(text1a, text2a, false, deadline);
        LinkedList<Diff> diffsb = diff_main(text1b, text2b, false, deadline);
        diffs.addAll(diffsb);
        return diffs;
    }

    protected LinesToCharsResult diff_linesToChars(String text1, String text2) {
        List<String> lineArray = new ArrayList<>();
        Map<String, Integer> lineHash = new HashMap<>();
        lineArray.add("");
        String chars1 = diff_linesToCharsMunge(text1, lineArray, lineHash, 40000);
        String chars2 = diff_linesToCharsMunge(text2, lineArray, lineHash, 65535);
        return new LinesToCharsResult(chars1, chars2, lineArray);
    }

    private String diff_linesToCharsMunge(String text, List<String> lineArray, Map<String, Integer> lineHash, int maxLines) {
        int lineStart = 0;
        int lineEnd = -1;
        StringBuilder chars = new StringBuilder();
        while (lineEnd < text.length() - 1) {
            lineEnd = text.indexOf(10, lineStart);
            if (lineEnd == -1) {
                lineEnd = text.length() - 1;
            }
            String line = text.substring(lineStart, lineEnd + 1);
            if (lineHash.containsKey(line)) {
                chars.append(String.valueOf((char) lineHash.get(line).intValue()));
            } else {
                if (lineArray.size() == maxLines) {
                    line = text.substring(lineStart);
                    lineEnd = text.length();
                }
                lineArray.add(line);
                lineHash.put(line, Integer.valueOf(lineArray.size() - 1));
                chars.append(String.valueOf((char) (lineArray.size() - 1)));
            }
            lineStart = lineEnd + 1;
        }
        return chars.toString();
    }

    protected void diff_charsToLines(List<Diff> diffs, List<String> lineArray) {
        for (Diff diff : diffs) {
            StringBuilder text = new StringBuilder();
            for (int j = 0; j < diff.text.length(); j++) {
                text.append(lineArray.get(diff.text.charAt(j)));
            }
            diff.text = text.toString();
        }
    }

    public int diff_commonPrefix(String text1, String text2) {
        int n = Math.min(text1.length(), text2.length());
        for (int i = 0; i < n; i++) {
            if (text1.charAt(i) != text2.charAt(i)) {
                return i;
            }
        }
        return n;
    }

    public int diff_commonSuffix(String text1, String text2) {
        int text1_length = text1.length();
        int text2_length = text2.length();
        int n = Math.min(text1_length, text2_length);
        for (int i = 1; i <= n; i++) {
            if (text1.charAt(text1_length - i) != text2.charAt(text2_length - i)) {
                return i - 1;
            }
        }
        return n;
    }

    protected int diff_commonOverlap(String text1, String text2) {
        String text12;
        String text22;
        int text1_length = text1.length();
        int text2_length = text2.length();
        if (text1_length == 0 || text2_length == 0) {
            return 0;
        }
        if (text1_length > text2_length) {
            text12 = text1.substring(text1_length - text2_length);
            text22 = text2;
        } else if (text1_length >= text2_length) {
            text12 = text1;
            text22 = text2;
        } else {
            text12 = text1;
            text22 = text2.substring(0, text1_length);
        }
        int text_length = Math.min(text1_length, text2_length);
        if (text12.equals(text22)) {
            return text_length;
        }
        int best = 0;
        int length = 1;
        while (true) {
            String pattern = text12.substring(text_length - length);
            int found = text22.indexOf(pattern);
            if (found == -1) {
                return best;
            }
            length += found;
            if (found == 0 || text12.substring(text_length - length).equals(text22.substring(0, length))) {
                best = length;
                length++;
            }
        }
    }

    protected String[] diff_halfMatch(String text1, String text2) {
        String[] hm;
        if (this.Diff_Timeout <= 0.0f) {
            return null;
        }
        String longtext = text1.length() > text2.length() ? text1 : text2;
        String shorttext = text1.length() > text2.length() ? text2 : text1;
        if (longtext.length() < 4 || shorttext.length() * 2 < longtext.length()) {
            return null;
        }
        String[] hm1 = diff_halfMatchI(longtext, shorttext, (longtext.length() + 3) / 4);
        String[] hm2 = diff_halfMatchI(longtext, shorttext, (longtext.length() + 1) / 2);
        if (hm1 == null && hm2 == null) {
            return null;
        }
        if (hm2 == null) {
            hm = hm1;
        } else if (hm1 == null) {
            hm = hm2;
        } else {
            hm = hm1[4].length() > hm2[4].length() ? hm1 : hm2;
        }
        return text1.length() > text2.length() ? hm : new String[]{hm[2], hm[3], hm[0], hm[1], hm[4]};
    }

    private String[] diff_halfMatchI(String longtext, String shorttext, int i) {
        String seed = longtext.substring(i, (longtext.length() / 4) + i);
        int j = -1;
        String best_longtext_a = "";
        String best_longtext_b = "";
        String best_shorttext_a = "";
        String best_shorttext_a2 = "";
        String best_shorttext_b = "";
        while (true) {
            int indexOf = shorttext.indexOf(seed, j + 1);
            j = indexOf;
            if (indexOf == -1) {
                break;
            }
            int prefixLength = diff_commonPrefix(longtext.substring(i), shorttext.substring(j));
            int suffixLength = diff_commonSuffix(longtext.substring(0, i), shorttext.substring(0, j));
            if (best_longtext_a.length() < suffixLength + prefixLength) {
                String best_common = shorttext.substring(j - suffixLength, j) + shorttext.substring(j, j + prefixLength);
                String best_longtext_a2 = longtext.substring(0, i - suffixLength);
                String best_longtext_b2 = longtext.substring(i + prefixLength);
                String best_shorttext_a3 = shorttext.substring(0, j - suffixLength);
                best_shorttext_b = shorttext.substring(j + prefixLength);
                best_shorttext_a2 = best_shorttext_a3;
                best_shorttext_a = best_longtext_b2;
                best_longtext_b = best_longtext_a2;
                best_longtext_a = best_common;
            }
        }
        if (best_longtext_a.length() * 2 >= longtext.length()) {
            return new String[]{best_longtext_b, best_shorttext_a, best_shorttext_a2, best_shorttext_b, best_longtext_a};
        }
        return null;
    }

    public void diff_cleanupSemantic(LinkedList<Diff> diffs) {
        boolean changes;
        Deque<Diff> equalities;
        String lastEquality;
        DiffMatchPatchUtils diffMatchPatchUtils = this;
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes2 = false;
        Deque<Diff> equalities2 = new ArrayDeque<>();
        String lastEquality2 = null;
        ListIterator<Diff> pointer = diffs.listIterator();
        int length_insertions1 = 0;
        int length_deletions1 = 0;
        int length_insertions2 = 0;
        int length_deletions2 = 0;
        Diff thisDiff = pointer.next();
        while (thisDiff != null) {
            if (thisDiff.operation == Operation.EQUAL) {
                equalities2.push(thisDiff);
                length_insertions1 = length_insertions2;
                length_deletions1 = length_deletions2;
                length_insertions2 = 0;
                length_deletions2 = 0;
                lastEquality2 = thisDiff.text;
            } else {
                if (thisDiff.operation == Operation.INSERT) {
                    length_insertions2 += thisDiff.text.length();
                } else {
                    length_deletions2 += thisDiff.text.length();
                }
                if (lastEquality2 != null && lastEquality2.length() <= Math.max(length_insertions1, length_deletions1) && lastEquality2.length() <= Math.max(length_insertions2, length_deletions2)) {
                    while (thisDiff != equalities2.peek()) {
                        Diff thisDiff2 = pointer.previous();
                        thisDiff = thisDiff2;
                    }
                    pointer.next();
                    pointer.set(new Diff(Operation.DELETE, lastEquality2));
                    pointer.add(new Diff(Operation.INSERT, lastEquality2));
                    equalities2.pop();
                    if (!equalities2.isEmpty()) {
                        equalities2.pop();
                    }
                    if (equalities2.isEmpty()) {
                        while (pointer.hasPrevious()) {
                            pointer.previous();
                        }
                    } else {
                        Diff thisDiff3 = equalities2.peek();
                        do {
                        } while (thisDiff3 != pointer.previous());
                    }
                    length_insertions1 = 0;
                    length_insertions2 = 0;
                    length_deletions1 = 0;
                    length_deletions2 = 0;
                    lastEquality2 = null;
                    changes2 = true;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
        if (changes2) {
            diff_cleanupMerge(diffs);
        }
        diff_cleanupSemanticLossless(diffs);
        ListIterator<Diff> pointer2 = diffs.listIterator();
        Diff prevDiff = null;
        Diff thisDiff4 = null;
        if (pointer2.hasNext()) {
            Diff prevDiff2 = pointer2.next();
            prevDiff = prevDiff2;
            if (pointer2.hasNext()) {
                Diff thisDiff5 = pointer2.next();
                thisDiff4 = thisDiff5;
            }
        }
        while (thisDiff4 != null) {
            if (prevDiff.operation != Operation.DELETE || thisDiff4.operation != Operation.INSERT) {
                changes = changes2;
                equalities = equalities2;
                lastEquality = lastEquality2;
            } else {
                String deletion = prevDiff.text;
                String insertion = thisDiff4.text;
                int overlap_length1 = diffMatchPatchUtils.diff_commonOverlap(deletion, insertion);
                int overlap_length2 = diffMatchPatchUtils.diff_commonOverlap(insertion, deletion);
                if (overlap_length1 >= overlap_length2) {
                    Diff prevDiff3 = prevDiff;
                    changes = changes2;
                    if (overlap_length1 >= deletion.length() / 2.0d || overlap_length1 >= insertion.length() / 2.0d) {
                        pointer2.previous();
                        pointer2.add(new Diff(Operation.EQUAL, insertion.substring(0, overlap_length1)));
                        prevDiff3.text = deletion.substring(0, deletion.length() - overlap_length1);
                        thisDiff4.text = insertion.substring(overlap_length1);
                        equalities = equalities2;
                        lastEquality = lastEquality2;
                    } else {
                        lastEquality = lastEquality2;
                        equalities = equalities2;
                    }
                } else {
                    changes = changes2;
                    equalities = equalities2;
                    lastEquality = lastEquality2;
                    if (overlap_length2 >= deletion.length() / 2.0d || overlap_length2 >= insertion.length() / 2.0d) {
                        pointer2.previous();
                        pointer2.add(new Diff(Operation.EQUAL, deletion.substring(0, overlap_length2)));
                        prevDiff.operation = Operation.INSERT;
                        prevDiff.text = insertion.substring(0, insertion.length() - overlap_length2);
                        thisDiff4.operation = Operation.DELETE;
                        thisDiff4.text = deletion.substring(overlap_length2);
                    }
                }
                thisDiff4 = pointer2.hasNext() ? pointer2.next() : null;
            }
            prevDiff = thisDiff4;
            thisDiff4 = pointer2.hasNext() ? pointer2.next() : null;
            diffMatchPatchUtils = this;
            equalities2 = equalities;
            changes2 = changes;
            lastEquality2 = lastEquality;
        }
    }

    public void diff_cleanupSemanticLossless(LinkedList<Diff> diffs) {
        ListIterator<Diff> pointer = diffs.listIterator();
        Diff prevDiff = pointer.hasNext() ? pointer.next() : null;
        Diff thisDiff = pointer.hasNext() ? pointer.next() : null;
        Diff nextDiff = pointer.hasNext() ? pointer.next() : null;
        while (nextDiff != null) {
            if (prevDiff.operation == Operation.EQUAL && nextDiff.operation == Operation.EQUAL) {
                String equality1 = prevDiff.text;
                String edit = thisDiff.text;
                String equality2 = nextDiff.text;
                int commonOffset = diff_commonSuffix(equality1, edit);
                if (commonOffset != 0) {
                    String commonString = edit.substring(edit.length() - commonOffset);
                    equality1 = equality1.substring(0, equality1.length() - commonOffset);
                    edit = commonString + edit.substring(0, edit.length() - commonOffset);
                    equality2 = commonString + equality2;
                }
                String bestEquality1 = equality1;
                String bestEdit = edit;
                String bestEquality2 = equality2;
                int bestScore = diff_cleanupSemanticScore(equality1, edit) + diff_cleanupSemanticScore(edit, equality2);
                while (edit.length() != 0 && equality2.length() != 0) {
                    if (edit.charAt(0) != equality2.charAt(0)) {
                        break;
                    }
                    equality1 = equality1 + edit.charAt(0);
                    StringBuilder sb = new StringBuilder();
                    int commonOffset2 = commonOffset;
                    sb.append(edit.substring(1));
                    sb.append(equality2.charAt(0));
                    edit = sb.toString();
                    equality2 = equality2.substring(1);
                    int score = diff_cleanupSemanticScore(equality1, edit) + diff_cleanupSemanticScore(edit, equality2);
                    if (score < bestScore) {
                        commonOffset = commonOffset2;
                    } else {
                        bestScore = score;
                        bestEquality1 = equality1;
                        bestEdit = edit;
                        bestEquality2 = equality2;
                        commonOffset = commonOffset2;
                    }
                }
                if (!prevDiff.text.equals(bestEquality1)) {
                    if (bestEquality1.length() != 0) {
                        prevDiff.text = bestEquality1;
                    } else {
                        pointer.previous();
                        pointer.previous();
                        pointer.previous();
                        pointer.remove();
                        pointer.next();
                        pointer.next();
                    }
                    thisDiff.text = bestEdit;
                    if (bestEquality2.length() != 0) {
                        nextDiff.text = bestEquality2;
                    } else {
                        pointer.remove();
                        nextDiff = thisDiff;
                        thisDiff = prevDiff;
                    }
                }
            }
            prevDiff = thisDiff;
            thisDiff = nextDiff;
            nextDiff = pointer.hasNext() ? pointer.next() : null;
        }
    }

    private int diff_cleanupSemanticScore(String one, String two) {
        if (one.length() == 0 || two.length() == 0) {
            return 6;
        }
        char char1 = one.charAt(one.length() - 1);
        char char2 = two.charAt(0);
        boolean nonAlphaNumeric1 = !Character.isLetterOrDigit(char1);
        boolean nonAlphaNumeric2 = !Character.isLetterOrDigit(char2);
        boolean whitespace1 = nonAlphaNumeric1 && Character.isWhitespace(char1);
        boolean whitespace2 = nonAlphaNumeric2 && Character.isWhitespace(char2);
        boolean lineBreak1 = whitespace1 && Character.getType(char1) == 15;
        boolean lineBreak2 = whitespace2 && Character.getType(char2) == 15;
        boolean blankLine1 = lineBreak1 && this.BLANKLINEEND.matcher(one).find();
        boolean blankLine2 = lineBreak2 && this.BLANKLINESTART.matcher(two).find();
        if (blankLine1 || blankLine2) {
            return 5;
        }
        if (lineBreak1 || lineBreak2) {
            return 4;
        }
        if (nonAlphaNumeric1 && !whitespace1 && whitespace2) {
            return 3;
        }
        if (whitespace1 || whitespace2) {
            return 2;
        }
        return (nonAlphaNumeric1 || nonAlphaNumeric2) ? 1 : 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:43:0x007b, code lost:
        if (((((r4 ? 1 : 0) + (r5 ? 1 : 0)) + (r6 ? 1 : 0)) + (r7 ? 1 : 0)) == 3) goto L32;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void diff_cleanupEfficiency(java.util.LinkedList<com.xiaopeng.speech.vui.utils.DiffMatchPatchUtils.Diff> r15) {
        /*
            Method dump skipped, instructions count: 235
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.utils.DiffMatchPatchUtils.diff_cleanupEfficiency(java.util.LinkedList):void");
    }

    public void diff_cleanupMerge(LinkedList<Diff> diffs) {
        Diff thisDiff;
        diffs.add(new Diff(Operation.EQUAL, ""));
        ListIterator<Diff> pointer = diffs.listIterator();
        int count_delete = 0;
        int count_insert = 0;
        String text_delete = "";
        String text_insert = "";
        Diff thisDiff2 = pointer.next();
        Diff prevEqual = null;
        while (thisDiff2 != null) {
            int i = AnonymousClass1.$SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation[thisDiff2.operation.ordinal()];
            boolean z = true;
            if (i == 1) {
                count_insert++;
                text_insert = text_insert + thisDiff2.text;
                prevEqual = null;
            } else if (i == 2) {
                count_delete++;
                text_delete = text_delete + thisDiff2.text;
                prevEqual = null;
            } else if (i == 3) {
                if (count_delete + count_insert > 1) {
                    if (count_delete == 0 || count_insert == 0) {
                        z = false;
                    }
                    boolean both_types = z;
                    pointer.previous();
                    while (true) {
                        int count_delete2 = count_delete - 1;
                        if (count_delete <= 0) {
                            break;
                        }
                        pointer.previous();
                        pointer.remove();
                        count_delete = count_delete2;
                    }
                    while (true) {
                        int count_insert2 = count_insert - 1;
                        if (count_insert <= 0) {
                            break;
                        }
                        pointer.previous();
                        pointer.remove();
                        count_insert = count_insert2;
                    }
                    if (both_types) {
                        int commonlength = diff_commonPrefix(text_insert, text_delete);
                        if (commonlength != 0) {
                            if (pointer.hasPrevious()) {
                                Diff thisDiff3 = pointer.previous();
                                Diff thisDiff4 = thisDiff3;
                                thisDiff4.text += text_insert.substring(0, commonlength);
                                pointer.next();
                            } else {
                                pointer.add(new Diff(Operation.EQUAL, text_insert.substring(0, commonlength)));
                            }
                            text_insert = text_insert.substring(commonlength);
                            text_delete = text_delete.substring(commonlength);
                        }
                        int commonlength2 = diff_commonSuffix(text_insert, text_delete);
                        if (commonlength2 != 0) {
                            Diff thisDiff5 = pointer.next();
                            thisDiff5.text = text_insert.substring(text_insert.length() - commonlength2) + thisDiff.text;
                            text_insert = text_insert.substring(0, text_insert.length() - commonlength2);
                            text_delete = text_delete.substring(0, text_delete.length() - commonlength2);
                            pointer.previous();
                        }
                    }
                    if (text_delete.length() != 0) {
                        pointer.add(new Diff(Operation.DELETE, text_delete));
                    }
                    if (text_insert.length() != 0) {
                        pointer.add(new Diff(Operation.INSERT, text_insert));
                    }
                    Diff thisDiff6 = pointer.hasNext() ? pointer.next() : null;
                    thisDiff2 = thisDiff6;
                } else if (prevEqual != null) {
                    prevEqual.text += thisDiff2.text;
                    pointer.remove();
                    Diff thisDiff7 = pointer.previous();
                    thisDiff2 = thisDiff7;
                    pointer.next();
                }
                count_insert = 0;
                count_delete = 0;
                text_delete = "";
                text_insert = "";
                prevEqual = thisDiff2;
            }
            thisDiff2 = pointer.hasNext() ? pointer.next() : null;
        }
        if (diffs.getLast().text.length() == 0) {
            diffs.removeLast();
        }
        boolean changes = false;
        ListIterator<Diff> pointer2 = diffs.listIterator();
        Diff prevDiff = pointer2.hasNext() ? pointer2.next() : null;
        Diff thisDiff8 = pointer2.hasNext() ? pointer2.next() : null;
        Diff nextDiff = pointer2.hasNext() ? pointer2.next() : null;
        while (nextDiff != null) {
            if (prevDiff.operation == Operation.EQUAL && nextDiff.operation == Operation.EQUAL) {
                if (thisDiff8.text.endsWith(prevDiff.text)) {
                    thisDiff8.text = prevDiff.text + thisDiff8.text.substring(0, thisDiff8.text.length() - prevDiff.text.length());
                    nextDiff.text = prevDiff.text + nextDiff.text;
                    pointer2.previous();
                    pointer2.previous();
                    pointer2.previous();
                    pointer2.remove();
                    pointer2.next();
                    Diff thisDiff9 = pointer2.next();
                    thisDiff8 = thisDiff9;
                    nextDiff = pointer2.hasNext() ? pointer2.next() : null;
                    changes = true;
                } else if (thisDiff8.text.startsWith(nextDiff.text)) {
                    prevDiff.text += nextDiff.text;
                    thisDiff8.text = thisDiff8.text.substring(nextDiff.text.length()) + nextDiff.text;
                    pointer2.remove();
                    nextDiff = pointer2.hasNext() ? pointer2.next() : null;
                    changes = true;
                }
            }
            prevDiff = thisDiff8;
            thisDiff8 = nextDiff;
            nextDiff = pointer2.hasNext() ? pointer2.next() : null;
        }
        if (changes) {
            diff_cleanupMerge(diffs);
        }
    }

    public int diff_xIndex(List<Diff> diffs, int loc) {
        int chars1 = 0;
        int chars2 = 0;
        int last_chars1 = 0;
        int last_chars2 = 0;
        Diff lastDiff = null;
        Iterator<Diff> it = diffs.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Diff aDiff = it.next();
            if (aDiff.operation != Operation.INSERT) {
                chars1 += aDiff.text.length();
            }
            if (aDiff.operation != Operation.DELETE) {
                chars2 += aDiff.text.length();
            }
            if (chars1 > loc) {
                lastDiff = aDiff;
                break;
            }
            last_chars1 = chars1;
            last_chars2 = chars2;
        }
        if (lastDiff != null && lastDiff.operation == Operation.DELETE) {
            return last_chars2;
        }
        return (loc - last_chars1) + last_chars2;
    }

    public String diff_prettyHtml(List<Diff> diffs) {
        StringBuilder html = new StringBuilder();
        for (Diff aDiff : diffs) {
            String text = aDiff.text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "&para;<br>");
            int i = AnonymousClass1.$SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation[aDiff.operation.ordinal()];
            if (i == 1) {
                html.append("<ins style=\"background:#e6ffe6;\">");
                html.append(text);
                html.append("</ins>");
            } else if (i == 2) {
                html.append("<del style=\"background:#ffe6e6;\">");
                html.append(text);
                html.append("</del>");
            } else if (i == 3) {
                html.append("<span>");
                html.append(text);
                html.append("</span>");
            }
        }
        return html.toString();
    }

    public String diff_text1(List<Diff> diffs) {
        StringBuilder text = new StringBuilder();
        for (Diff aDiff : diffs) {
            if (aDiff.operation != Operation.INSERT) {
                text.append(aDiff.text);
            }
        }
        return text.toString();
    }

    public String diff_text2(List<Diff> diffs) {
        StringBuilder text = new StringBuilder();
        for (Diff aDiff : diffs) {
            if (aDiff.operation != Operation.DELETE) {
                text.append(aDiff.text);
            }
        }
        return text.toString();
    }

    public int diff_levenshtein(List<Diff> diffs) {
        int levenshtein = 0;
        int insertions = 0;
        int deletions = 0;
        for (Diff aDiff : diffs) {
            int i = AnonymousClass1.$SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation[aDiff.operation.ordinal()];
            if (i == 1) {
                insertions += aDiff.text.length();
            } else if (i == 2) {
                deletions += aDiff.text.length();
            } else if (i == 3) {
                levenshtein += Math.max(insertions, deletions);
                insertions = 0;
                deletions = 0;
            }
        }
        return levenshtein + Math.max(insertions, deletions);
    }

    public String diff_toDelta(List<Diff> diffs) {
        StringBuilder text = new StringBuilder();
        for (Diff aDiff : diffs) {
            int i = AnonymousClass1.$SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation[aDiff.operation.ordinal()];
            if (i == 1) {
                try {
                    text.append("+");
                    text.append(URLEncoder.encode(aDiff.text, Key.STRING_CHARSET_NAME).replace('+', ' '));
                    text.append("\t");
                } catch (UnsupportedEncodingException e) {
                    throw new Error("This system does not support UTF-8.", e);
                }
            } else if (i == 2) {
                text.append("-");
                text.append(aDiff.text.length());
                text.append("\t");
            } else if (i == 3) {
                text.append("=");
                text.append(aDiff.text.length());
                text.append("\t");
            }
        }
        String delta = text.toString();
        if (delta.length() != 0) {
            return unescapeForEncodeUriCompatability(delta.substring(0, delta.length() - 1));
        }
        return delta;
    }

    public LinkedList<Diff> diff_fromDelta(String text1, String delta) throws IllegalArgumentException {
        LinkedList<Diff> diffs = new LinkedList<>();
        String[] tokens = delta.split("\t");
        int pointer = 0;
        for (String token : tokens) {
            if (token.length() != 0) {
                String param = token.substring(1);
                char charAt = token.charAt(0);
                if (charAt == '+') {
                    String param2 = param.replace("+", "%2B");
                    try {
                        diffs.add(new Diff(Operation.INSERT, URLDecoder.decode(param2, Key.STRING_CHARSET_NAME)));
                    } catch (UnsupportedEncodingException e) {
                        throw new Error("This system does not support UTF-8.", e);
                    } catch (IllegalArgumentException e2) {
                        throw new IllegalArgumentException("Illegal escape in diff_fromDelta: " + param2, e2);
                    }
                } else if (charAt == '-' || charAt == '=') {
                    try {
                        int n = Integer.parseInt(param);
                        if (n < 0) {
                            throw new IllegalArgumentException("Negative number in diff_fromDelta: " + param);
                        }
                        int pointer2 = pointer + n;
                        try {
                            String text = text1.substring(pointer, pointer2);
                            if (token.charAt(0) == '=') {
                                diffs.add(new Diff(Operation.EQUAL, text));
                            } else {
                                diffs.add(new Diff(Operation.DELETE, text));
                            }
                            pointer = pointer2;
                        } catch (StringIndexOutOfBoundsException e3) {
                            throw new IllegalArgumentException("Delta length (" + pointer2 + ") larger than source text length (" + text1.length() + ").", e3);
                        }
                    } catch (NumberFormatException e4) {
                        throw new IllegalArgumentException("Invalid number in diff_fromDelta: " + param, e4);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid diff operation in diff_fromDelta: " + token.charAt(0));
                }
            }
        }
        if (pointer == text1.length()) {
            return diffs;
        }
        throw new IllegalArgumentException("Delta length (" + pointer + ") smaller than source text length (" + text1.length() + ").");
    }

    public int match_main(String text, String pattern, int loc) {
        if (text == null || pattern == null) {
            throw new IllegalArgumentException("Null inputs. (match_main)");
        }
        int loc2 = Math.max(0, Math.min(loc, text.length()));
        if (text.equals(pattern)) {
            return 0;
        }
        if (text.length() == 0) {
            return -1;
        }
        if (pattern.length() + loc2 <= text.length() && text.substring(loc2, pattern.length() + loc2).equals(pattern)) {
            return loc2;
        }
        return match_bitap(text, pattern, loc2);
    }

    protected int match_bitap(String text, String pattern, int loc) {
        Map<Character, Integer> s;
        int i;
        int charMatch;
        Map<Character, Integer> s2;
        String str = text;
        Map<Character, Integer> s3 = match_alphabet(pattern);
        double score_threshold = this.Match_Threshold;
        int best_loc = text.indexOf(pattern, loc);
        if (best_loc != -1) {
            score_threshold = Math.min(match_bitapScore(0, best_loc, loc, pattern), score_threshold);
            int best_loc2 = str.lastIndexOf(pattern, pattern.length() + loc);
            if (best_loc2 != -1) {
                score_threshold = Math.min(match_bitapScore(0, best_loc2, loc, pattern), score_threshold);
            }
        }
        int i2 = 1;
        int matchmask = 1 << (pattern.length() - 1);
        int best_loc3 = -1;
        int bin_max = pattern.length() + text.length();
        int[] last_rd = new int[0];
        int d = 0;
        while (d < pattern.length()) {
            int bin_min = 0;
            int bin_mid = bin_max;
            while (bin_min < bin_mid) {
                if (match_bitapScore(d, loc + bin_mid, loc, pattern) <= score_threshold) {
                    bin_min = bin_mid;
                } else {
                    bin_max = bin_mid;
                }
                bin_mid = ((bin_max - bin_min) / 2) + bin_min;
            }
            bin_max = bin_mid;
            int start = Math.max(i2, (loc - bin_mid) + i2);
            double score_threshold2 = score_threshold;
            int finish = Math.min(loc + bin_mid, text.length()) + pattern.length();
            int[] rd = new int[finish + 2];
            rd[finish + 1] = (1 << d) - 1;
            int j = finish;
            while (true) {
                if (j < start) {
                    s = s3;
                    i = 1;
                    break;
                }
                int finish2 = finish;
                int best_loc4 = best_loc3;
                if (text.length() <= j - 1 || !s3.containsKey(Character.valueOf(str.charAt(j - 1)))) {
                    charMatch = 0;
                } else {
                    charMatch = s3.get(Character.valueOf(str.charAt(j - 1))).intValue();
                }
                if (d == 0) {
                    rd[j] = ((rd[j + 1] << 1) | 1) & charMatch;
                } else {
                    rd[j] = (((rd[j + 1] << 1) | 1) & charMatch) | ((last_rd[j + 1] | last_rd[j]) << 1) | 1 | last_rd[j + 1];
                }
                if ((rd[j] & matchmask) != 0) {
                    double score = match_bitapScore(d, j - 1, loc, pattern);
                    if (score > score_threshold2) {
                        s2 = s3;
                    } else {
                        score_threshold2 = score;
                        best_loc3 = j - 1;
                        if (best_loc3 <= loc) {
                            s = s3;
                            i = 1;
                            break;
                        }
                        s2 = s3;
                        start = Math.max(1, (loc * 2) - best_loc3);
                        j--;
                        str = text;
                        finish = finish2;
                        s3 = s2;
                    }
                } else {
                    s2 = s3;
                }
                best_loc3 = best_loc4;
                j--;
                str = text;
                finish = finish2;
                s3 = s2;
            }
            if (match_bitapScore(d + 1, loc, loc, pattern) > score_threshold2) {
                break;
            }
            last_rd = rd;
            d++;
            str = text;
            i2 = i;
            score_threshold = score_threshold2;
            s3 = s;
        }
        return best_loc3;
    }

    private double match_bitapScore(int e, int x, int loc, String pattern) {
        float accuracy = e / pattern.length();
        int proximity = Math.abs(loc - x);
        int i = this.Match_Distance;
        if (i == 0) {
            if (proximity == 0) {
                return accuracy;
            }
            return 1.0d;
        }
        return (proximity / i) + accuracy;
    }

    protected Map<Character, Integer> match_alphabet(String pattern) {
        Map<Character, Integer> s = new HashMap<>();
        char[] char_pattern = pattern.toCharArray();
        for (char c : char_pattern) {
            s.put(Character.valueOf(c), 0);
        }
        int i = 0;
        for (char c2 : char_pattern) {
            s.put(Character.valueOf(c2), Integer.valueOf(s.get(Character.valueOf(c2)).intValue() | (1 << ((pattern.length() - i) - 1))));
            i++;
        }
        return s;
    }

    protected void patch_addContext(Patch patch, String text) {
        if (text.length() == 0) {
            return;
        }
        String pattern = text.substring(patch.start2, patch.start2 + patch.length1);
        int padding = 0;
        while (text.indexOf(pattern) != text.lastIndexOf(pattern)) {
            int length = pattern.length();
            short s = this.Match_MaxBits;
            short s2 = this.Patch_Margin;
            if (length >= (s - s2) - s2) {
                break;
            }
            padding += s2;
            pattern = text.substring(Math.max(0, patch.start2 - padding), Math.min(text.length(), patch.start2 + patch.length1 + padding));
        }
        int padding2 = padding + this.Patch_Margin;
        String prefix = text.substring(Math.max(0, patch.start2 - padding2), patch.start2);
        if (prefix.length() != 0) {
            patch.diffs.addFirst(new Diff(Operation.EQUAL, prefix));
        }
        String suffix = text.substring(patch.start2 + patch.length1, Math.min(text.length(), patch.start2 + patch.length1 + padding2));
        if (suffix.length() != 0) {
            patch.diffs.addLast(new Diff(Operation.EQUAL, suffix));
        }
        patch.start1 -= prefix.length();
        patch.start2 -= prefix.length();
        patch.length1 += prefix.length() + suffix.length();
        patch.length2 += prefix.length() + suffix.length();
    }

    public LinkedList<Patch> patch_make(String text1, String text2) {
        if (text1 == null || text2 == null) {
            throw new IllegalArgumentException("Null inputs. (patch_make)");
        }
        LinkedList<Diff> diffs = diff_main(text1, text2, true);
        if (diffs.size() > 2) {
            diff_cleanupSemantic(diffs);
            diff_cleanupEfficiency(diffs);
        }
        return patch_make(text1, diffs);
    }

    public LinkedList<Patch> patch_make(LinkedList<Diff> diffs) {
        if (diffs == null) {
            throw new IllegalArgumentException("Null inputs. (patch_make)");
        }
        String text1 = diff_text1(diffs);
        return patch_make(text1, diffs);
    }

    @Deprecated
    public LinkedList<Patch> patch_make(String text1, String text2, LinkedList<Diff> diffs) {
        return patch_make(text1, diffs);
    }

    public LinkedList<Patch> patch_make(String text1, LinkedList<Diff> diffs) {
        if (text1 == null || diffs == null) {
            throw new IllegalArgumentException("Null inputs. (patch_make)");
        }
        LinkedList<Patch> patches = new LinkedList<>();
        if (diffs.isEmpty()) {
            return patches;
        }
        Patch patch = new Patch();
        int char_count1 = 0;
        int char_count2 = 0;
        String prepatch_text = text1;
        String postpatch_text = text1;
        Iterator<Diff> it = diffs.iterator();
        while (it.hasNext()) {
            Diff aDiff = it.next();
            if (patch.diffs.isEmpty() && aDiff.operation != Operation.EQUAL) {
                patch.start1 = char_count1;
                patch.start2 = char_count2;
            }
            int i = AnonymousClass1.$SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation[aDiff.operation.ordinal()];
            if (i == 1) {
                patch.diffs.add(aDiff);
                patch.length2 += aDiff.text.length();
                postpatch_text = postpatch_text.substring(0, char_count2) + aDiff.text + postpatch_text.substring(char_count2);
            } else if (i == 2) {
                patch.length1 += aDiff.text.length();
                patch.diffs.add(aDiff);
                postpatch_text = postpatch_text.substring(0, char_count2) + postpatch_text.substring(aDiff.text.length() + char_count2);
            } else if (i == 3) {
                if (aDiff.text.length() <= this.Patch_Margin * 2 && !patch.diffs.isEmpty() && aDiff != diffs.getLast()) {
                    patch.diffs.add(aDiff);
                    patch.length1 += aDiff.text.length();
                    patch.length2 += aDiff.text.length();
                }
                if (aDiff.text.length() >= this.Patch_Margin * 2 && !patch.diffs.isEmpty() && !patch.diffs.isEmpty()) {
                    patch_addContext(patch, prepatch_text);
                    patches.add(patch);
                    patch = new Patch();
                    prepatch_text = postpatch_text;
                    char_count1 = char_count2;
                }
            }
            if (aDiff.operation != Operation.INSERT) {
                char_count1 += aDiff.text.length();
            }
            if (aDiff.operation != Operation.DELETE) {
                char_count2 += aDiff.text.length();
            }
        }
        if (!patch.diffs.isEmpty()) {
            patch_addContext(patch, prepatch_text);
            patches.add(patch);
        }
        return patches;
    }

    public LinkedList<Patch> patch_deepCopy(LinkedList<Patch> patches) {
        LinkedList<Patch> patchesCopy = new LinkedList<>();
        Iterator<Patch> it = patches.iterator();
        while (it.hasNext()) {
            Patch aPatch = it.next();
            Patch patchCopy = new Patch();
            Iterator<Diff> it2 = aPatch.diffs.iterator();
            while (it2.hasNext()) {
                Diff aDiff = it2.next();
                Diff diffCopy = new Diff(aDiff.operation, aDiff.text);
                patchCopy.diffs.add(diffCopy);
            }
            patchCopy.start1 = aPatch.start1;
            patchCopy.start2 = aPatch.start2;
            patchCopy.length1 = aPatch.length1;
            patchCopy.length2 = aPatch.length2;
            patchesCopy.add(patchCopy);
        }
        return patchesCopy;
    }

    public Object[] patch_apply(LinkedList<Patch> patches, String text) {
        int start_loc;
        String text2;
        LinkedList<Patch> patches2;
        Iterator<Patch> it;
        Patch aPatch;
        int expected_loc;
        int i = 0;
        if (patches.isEmpty()) {
            return new Object[]{text, new boolean[0]};
        }
        LinkedList<Patch> patches3 = patch_deepCopy(patches);
        String nullPadding = patch_addPadding(patches3);
        String text3 = nullPadding + text + nullPadding;
        patch_splitMax(patches3);
        int x = 0;
        int delta = 0;
        boolean[] results = new boolean[patches3.size()];
        Iterator<Patch> it2 = patches3.iterator();
        while (it2.hasNext()) {
            Patch aPatch2 = it2.next();
            int expected_loc2 = aPatch2.start2 + delta;
            String text1 = diff_text1(aPatch2.diffs);
            int end_loc = -1;
            int length = text1.length();
            int i2 = this.Match_MaxBits;
            if (length > i2) {
                start_loc = match_main(text3, text1.substring(i, i2), expected_loc2);
                if (start_loc != -1 && ((end_loc = match_main(text3, text1.substring(text1.length() - this.Match_MaxBits), (text1.length() + expected_loc2) - this.Match_MaxBits)) == -1 || start_loc >= end_loc)) {
                    start_loc = -1;
                }
            } else {
                start_loc = match_main(text3, text1, expected_loc2);
            }
            if (start_loc == -1) {
                results[x] = false;
                delta -= aPatch2.length2 - aPatch2.length1;
                patches2 = patches3;
                it = it2;
            } else {
                results[x] = true;
                int delta2 = start_loc - expected_loc2;
                if (end_loc == -1) {
                    text2 = text3.substring(start_loc, Math.min(text1.length() + start_loc, text3.length()));
                } else {
                    text2 = text3.substring(start_loc, Math.min(this.Match_MaxBits + end_loc, text3.length()));
                }
                if (text1.equals(text2)) {
                    StringBuilder sb = new StringBuilder();
                    patches2 = patches3;
                    sb.append(text3.substring(0, start_loc));
                    sb.append(diff_text2(aPatch2.diffs));
                    sb.append(text3.substring(text1.length() + start_loc));
                    text3 = sb.toString();
                    delta = delta2;
                    it = it2;
                } else {
                    patches2 = patches3;
                    LinkedList<Diff> diffs = diff_main(text1, text2, false);
                    if (text1.length() > this.Match_MaxBits && diff_levenshtein(diffs) / text1.length() > this.Patch_DeleteThreshold) {
                        results[x] = false;
                        delta = delta2;
                        it = it2;
                    } else {
                        diff_cleanupSemanticLossless(diffs);
                        int index1 = 0;
                        Iterator<Diff> it3 = aPatch2.diffs.iterator();
                        while (it3.hasNext()) {
                            int delta3 = delta2;
                            Diff aDiff = it3.next();
                            String text22 = text2;
                            Iterator<Patch> it4 = it2;
                            if (aDiff.operation == Operation.EQUAL) {
                                aPatch = aPatch2;
                                expected_loc = expected_loc2;
                            } else {
                                int index2 = diff_xIndex(diffs, index1);
                                aPatch = aPatch2;
                                if (aDiff.operation == Operation.INSERT) {
                                    StringBuilder sb2 = new StringBuilder();
                                    expected_loc = expected_loc2;
                                    sb2.append(text3.substring(0, start_loc + index2));
                                    sb2.append(aDiff.text);
                                    sb2.append(text3.substring(start_loc + index2));
                                    text3 = sb2.toString();
                                } else {
                                    expected_loc = expected_loc2;
                                    if (aDiff.operation == Operation.DELETE) {
                                        text3 = text3.substring(0, start_loc + index2) + text3.substring(diff_xIndex(diffs, aDiff.text.length() + index1) + start_loc);
                                    }
                                }
                            }
                            if (aDiff.operation != Operation.DELETE) {
                                index1 += aDiff.text.length();
                            }
                            delta2 = delta3;
                            text2 = text22;
                            it2 = it4;
                            aPatch2 = aPatch;
                            expected_loc2 = expected_loc;
                        }
                        it = it2;
                        delta = delta2;
                    }
                }
            }
            x++;
            patches3 = patches2;
            it2 = it;
            i = 0;
        }
        return new Object[]{text3.substring(nullPadding.length(), text3.length() - nullPadding.length()), results};
    }

    public String patch_addPadding(LinkedList<Patch> patches) {
        short paddingLength = this.Patch_Margin;
        String nullPadding = "";
        for (short x = 1; x <= paddingLength; x = (short) (x + 1)) {
            nullPadding = nullPadding + String.valueOf((char) x);
        }
        Iterator<Patch> it = patches.iterator();
        while (it.hasNext()) {
            Patch aPatch = it.next();
            aPatch.start1 += paddingLength;
            aPatch.start2 += paddingLength;
        }
        Patch patch = patches.getFirst();
        LinkedList<Diff> diffs = patch.diffs;
        if (diffs.isEmpty() || diffs.getFirst().operation != Operation.EQUAL) {
            diffs.addFirst(new Diff(Operation.EQUAL, nullPadding));
            patch.start1 -= paddingLength;
            patch.start2 -= paddingLength;
            patch.length1 += paddingLength;
            patch.length2 += paddingLength;
        } else if (paddingLength > diffs.getFirst().text.length()) {
            Diff firstDiff = diffs.getFirst();
            int extraLength = paddingLength - firstDiff.text.length();
            firstDiff.text = nullPadding.substring(firstDiff.text.length()) + firstDiff.text;
            patch.start1 = patch.start1 - extraLength;
            patch.start2 = patch.start2 - extraLength;
            patch.length1 = patch.length1 + extraLength;
            patch.length2 += extraLength;
        }
        Patch patch2 = patches.getLast();
        LinkedList<Diff> diffs2 = patch2.diffs;
        if (diffs2.isEmpty() || diffs2.getLast().operation != Operation.EQUAL) {
            diffs2.addLast(new Diff(Operation.EQUAL, nullPadding));
            patch2.length1 += paddingLength;
            patch2.length2 += paddingLength;
        } else if (paddingLength > diffs2.getLast().text.length()) {
            Diff lastDiff = diffs2.getLast();
            int extraLength2 = paddingLength - lastDiff.text.length();
            lastDiff.text += nullPadding.substring(0, extraLength2);
            patch2.length1 += extraLength2;
            patch2.length2 += extraLength2;
        }
        return nullPadding;
    }

    public void patch_splitMax(LinkedList<Patch> patches) {
        short patch_size = this.Match_MaxBits;
        ListIterator<Patch> pointer = patches.listIterator();
        Patch bigpatch = pointer.hasNext() ? pointer.next() : null;
        while (bigpatch != null) {
            if (bigpatch.length1 <= this.Match_MaxBits) {
                bigpatch = pointer.hasNext() ? pointer.next() : null;
            } else {
                pointer.remove();
                int start1 = bigpatch.start1;
                int start2 = bigpatch.start2;
                String precontext = "";
                while (!bigpatch.diffs.isEmpty()) {
                    Patch patch = new Patch();
                    boolean empty = true;
                    patch.start1 = start1 - precontext.length();
                    patch.start2 = start2 - precontext.length();
                    if (precontext.length() != 0) {
                        int length = precontext.length();
                        patch.length2 = length;
                        patch.length1 = length;
                        patch.diffs.add(new Diff(Operation.EQUAL, precontext));
                    }
                    while (!bigpatch.diffs.isEmpty() && patch.length1 < patch_size - this.Patch_Margin) {
                        Operation diff_type = bigpatch.diffs.getFirst().operation;
                        String diff_text = bigpatch.diffs.getFirst().text;
                        if (diff_type == Operation.INSERT) {
                            patch.length2 += diff_text.length();
                            start2 += diff_text.length();
                            patch.diffs.addLast(bigpatch.diffs.removeFirst());
                            empty = false;
                        } else if (diff_type != Operation.DELETE || patch.diffs.size() != 1 || patch.diffs.getFirst().operation != Operation.EQUAL || diff_text.length() <= patch_size * 2) {
                            String diff_text2 = diff_text.substring(0, Math.min(diff_text.length(), (patch_size - patch.length1) - this.Patch_Margin));
                            patch.length1 += diff_text2.length();
                            start1 += diff_text2.length();
                            if (diff_type == Operation.EQUAL) {
                                patch.length2 += diff_text2.length();
                                start2 += diff_text2.length();
                            } else {
                                empty = false;
                            }
                            patch.diffs.add(new Diff(diff_type, diff_text2));
                            if (diff_text2.equals(bigpatch.diffs.getFirst().text)) {
                                bigpatch.diffs.removeFirst();
                            } else {
                                bigpatch.diffs.getFirst().text = bigpatch.diffs.getFirst().text.substring(diff_text2.length());
                            }
                        } else {
                            patch.length1 += diff_text.length();
                            start1 += diff_text.length();
                            empty = false;
                            patch.diffs.add(new Diff(diff_type, diff_text));
                            bigpatch.diffs.removeFirst();
                        }
                    }
                    String precontext2 = diff_text2(patch.diffs);
                    precontext = precontext2.substring(Math.max(0, precontext2.length() - this.Patch_Margin));
                    String postcontext = diff_text1(bigpatch.diffs).length() > this.Patch_Margin ? diff_text1(bigpatch.diffs).substring(0, this.Patch_Margin) : diff_text1(bigpatch.diffs);
                    if (postcontext.length() != 0) {
                        patch.length1 += postcontext.length();
                        patch.length2 += postcontext.length();
                        if (!patch.diffs.isEmpty() && patch.diffs.getLast().operation == Operation.EQUAL) {
                            StringBuilder sb = new StringBuilder();
                            Diff last = patch.diffs.getLast();
                            sb.append(last.text);
                            sb.append(postcontext);
                            last.text = sb.toString();
                        } else {
                            patch.diffs.add(new Diff(Operation.EQUAL, postcontext));
                        }
                    }
                    if (!empty) {
                        pointer.add(patch);
                    }
                }
                bigpatch = pointer.hasNext() ? pointer.next() : null;
            }
        }
    }

    public String patch_toText(List<Patch> patches) {
        StringBuilder text = new StringBuilder();
        for (Patch aPatch : patches) {
            text.append(aPatch);
        }
        return text.toString();
    }

    public List<Patch> patch_fromText(String textline) throws IllegalArgumentException {
        char sign;
        String line;
        List<Patch> patches = new LinkedList<>();
        if (textline.length() == 0) {
            return patches;
        }
        List<String> textList = Arrays.asList(textline.split("\n"));
        LinkedList<String> text = new LinkedList<>(textList);
        Pattern patchHeader = Pattern.compile("^@@ -(\\d+),?(\\d*) \\+(\\d+),?(\\d*) @@$");
        while (!text.isEmpty()) {
            Matcher m = patchHeader.matcher(text.getFirst());
            if (!m.matches()) {
                throw new IllegalArgumentException("Invalid patch string: " + text.getFirst());
            }
            Patch patch = new Patch();
            patches.add(patch);
            patch.start1 = Integer.parseInt(m.group(1));
            if (m.group(2).length() == 0) {
                patch.start1--;
                patch.length1 = 1;
            } else if (!m.group(2).equals("0")) {
                patch.start1--;
                patch.length1 = Integer.parseInt(m.group(2));
            } else {
                patch.length1 = 0;
            }
            patch.start2 = Integer.parseInt(m.group(3));
            if (m.group(4).length() == 0) {
                patch.start2--;
                patch.length2 = 1;
            } else if (!m.group(4).equals("0")) {
                patch.start2--;
                patch.length2 = Integer.parseInt(m.group(4));
            } else {
                patch.length2 = 0;
            }
            text.removeFirst();
            while (true) {
                if (!text.isEmpty()) {
                    try {
                        sign = text.getFirst().charAt(0);
                        line = text.getFirst().substring(1).replace("+", "%2B");
                    } catch (IndexOutOfBoundsException e) {
                        text.removeFirst();
                    }
                    try {
                        String line2 = URLDecoder.decode(line, Key.STRING_CHARSET_NAME);
                        if (sign == '-') {
                            patch.diffs.add(new Diff(Operation.DELETE, line2));
                        } else if (sign == '+') {
                            patch.diffs.add(new Diff(Operation.INSERT, line2));
                        } else if (sign == ' ') {
                            patch.diffs.add(new Diff(Operation.EQUAL, line2));
                        } else if (sign != '@') {
                            throw new IllegalArgumentException("Invalid patch mode '" + sign + "' in: " + line2);
                        }
                        text.removeFirst();
                    } catch (UnsupportedEncodingException e2) {
                        throw new Error("This system does not support UTF-8.", e2);
                    } catch (IllegalArgumentException e3) {
                        throw new IllegalArgumentException("Illegal escape in patch_fromText: " + line, e3);
                    }
                }
            }
        }
        return patches;
    }

    /* loaded from: classes.dex */
    public static class Diff {
        public Operation operation;
        public String text;

        public Diff(Operation operation, String text) {
            this.operation = operation;
            this.text = text;
        }

        public String toString() {
            String prettyText = this.text.replace('\n', Typography.paragraph);
            return "Diff(" + this.operation + ",\"" + prettyText + "\")";
        }

        public int hashCode() {
            Operation operation = this.operation;
            int result = operation == null ? 0 : operation.hashCode();
            String str = this.text;
            return result + ((str != null ? str.hashCode() : 0) * 31);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Diff other = (Diff) obj;
            if (this.operation != other.operation) {
                return false;
            }
            String str = this.text;
            if (str == null) {
                if (other.text != null) {
                    return false;
                }
            } else if (!str.equals(other.text)) {
                return false;
            }
            return true;
        }
    }

    /* loaded from: classes.dex */
    public static class Patch {
        public LinkedList<Diff> diffs = new LinkedList<>();
        public int length1;
        public int length2;
        public int start1;
        public int start2;

        public String toString() {
            String coords1;
            String coords2;
            int i = this.length1;
            if (i == 0) {
                coords1 = this.start1 + ",0";
            } else if (i == 1) {
                coords1 = Integer.toString(this.start1 + 1);
            } else {
                coords1 = (this.start1 + 1) + "," + this.length1;
            }
            int i2 = this.length2;
            if (i2 == 0) {
                coords2 = this.start2 + ",0";
            } else if (i2 == 1) {
                coords2 = Integer.toString(this.start2 + 1);
            } else {
                coords2 = (this.start2 + 1) + "," + this.length2;
            }
            StringBuilder text = new StringBuilder();
            text.append("@@ -");
            text.append(coords1);
            text.append(" +");
            text.append(coords2);
            text.append(" @@\n");
            Iterator<Diff> it = this.diffs.iterator();
            while (it.hasNext()) {
                Diff aDiff = it.next();
                int i3 = AnonymousClass1.$SwitchMap$com$xiaopeng$speech$vui$utils$DiffMatchPatchUtils$Operation[aDiff.operation.ordinal()];
                if (i3 == 1) {
                    text.append('+');
                } else if (i3 == 2) {
                    text.append('-');
                } else if (i3 == 3) {
                    text.append(' ');
                }
                try {
                    text.append(URLEncoder.encode(aDiff.text, Key.STRING_CHARSET_NAME).replace('+', ' '));
                    text.append("\n");
                } catch (UnsupportedEncodingException e) {
                    throw new Error("This system does not support UTF-8.", e);
                }
            }
            return DiffMatchPatchUtils.unescapeForEncodeUriCompatability(text.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String unescapeForEncodeUriCompatability(String str) {
        return str.replace("%21", "!").replace("%7E", "~").replace("%27", "'").replace("%28", NavigationBarInflaterView.KEY_CODE_START).replace("%29", NavigationBarInflaterView.KEY_CODE_END).replace("%3B", NavigationBarInflaterView.GRAVITY_SEPARATOR).replace("%2F", "/").replace("%3F", "?").replace("%3A", NavigationBarInflaterView.KEY_IMAGE_DELIM).replace("%40", "@").replace("%26", "&").replace("%3D", "=").replace("%2B", "+").replace("%24", "$").replace("%2C", ",").replace("%23", "#");
    }

    public static String diffAndMerge(String str1, String str2) {
        DiffMatchPatchUtils utils = new DiffMatchPatchUtils();
        LinkedList<Diff> fis = utils.diff_compute(str1, str2, true, 2147483647L);
        Iterator<Diff> it = fis.iterator();
        while (it.hasNext()) {
            Diff diff = it.next();
            LogUtils.logDebug("VuiSceneCache", diff.operation + "========" + diff.text);
            PrintStream printStream = System.out;
            printStream.println(diff.operation + "========" + diff.text);
        }
        LinkedList<Patch> patches = utils.patch_make(str1, fis);
        String patchesStr = utils.patch_toText(patches);
        LinkedList<Patch> patches2 = (LinkedList) utils.patch_fromText(patchesStr);
        Object[] results = utils.patch_apply(patches2, str1);
        return (String) results[0];
    }
}
