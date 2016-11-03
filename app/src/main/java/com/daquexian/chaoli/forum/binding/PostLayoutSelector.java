package com.daquexian.chaoli.forum.binding;

import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.model.Post;

/**
 * Created by jianhao on 16-9-27.
 */

public class PostLayoutSelector extends LayoutSelector<Post> {
    @Override
    int getType(Post item) {
        if (item.deleteMemberId != 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    int getLayout(int type) {
        switch (type) {
            case 0:
                return R.layout.post_view;
            case 1:
                return R.layout.post_view_delete;
        }
        return 0;
    }
}
