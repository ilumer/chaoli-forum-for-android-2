package com.geno.chaoli.forum.utils;

import android.databinding.ObservableList;
import android.util.Log;

import com.geno.chaoli.forum.model.IExpanded;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by jianhao on 16-10-2.
 */

public class MyUtils {
    /**
     * 针对可能有其他帖子被顶到最上方，导致下一页的主题帖与这一页的主题帖有重合的现象
     * @param A 已有的主题帖列表
     * @param B 下一页主题帖列表
     * @return 合成后的新列表的长度
     */
    public static <T extends Comparable> int expandUnique(List<T> A, List<T> B) {
        return expandUnique(A, B, true);
    }

    public static <T extends Comparable> int expandUnique(List<T> A, List<T> B, Boolean addBehind) {
        int lenA = A.size();
        if (lenA == 0) {
            A.addAll(B);
        }
        if (addBehind) {
            int i;
            for (i = 0; i < B.size(); i++)
                if (B.get(i).compareTo(A.get(A.size() - 1)) > 0)
                    break;
            A.addAll(B.subList(i, B.size()));
        } else {
            int i;
            for (i = 0; i < B.size(); i++)
                if (B.get(i).compareTo(A.get(0)) >= 0)
                    break;
            A.addAll(0, B.subList(0, i));
        }
        return A.size();
    }
}
