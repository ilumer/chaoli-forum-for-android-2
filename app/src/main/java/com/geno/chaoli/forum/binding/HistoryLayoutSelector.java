package com.geno.chaoli.forum.binding;

import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.model.BusinessHomepageListItem;
import com.geno.chaoli.forum.viewmodel.HistoryFragmentVM;

/**
 * Created by jianhao on 16-10-2.
 */

public class HistoryLayoutSelector extends LayoutSelector<BusinessHomepageListItem> {
    @Override
    int getLayout(int type) {
        switch (type) {
            case 0:
                return R.layout.history_item;
            case 1:
                return R.layout.history_divider;
            case 2:
                return R.layout.history_space;
            default:
                throw new RuntimeException("Impossible");
        }
    }

    @Override
    int getType(BusinessHomepageListItem item) {
        if (item.getType().equals(HistoryFragmentVM.ListItem.DIVIDER)) return 1;
        else if (item.getType().equals(HistoryFragmentVM.ListItem.SPACE)) return 2;
        else return 0;
    }
}
