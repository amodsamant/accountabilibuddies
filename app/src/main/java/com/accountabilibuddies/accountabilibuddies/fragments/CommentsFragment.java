package com.accountabilibuddies.accountabilibuddies.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.accountabilibuddies.accountabilibuddies.R;
import com.accountabilibuddies.accountabilibuddies.adapter.CommentsAdapter;
import com.accountabilibuddies.accountabilibuddies.application.ParseApplication;
import com.accountabilibuddies.accountabilibuddies.databinding.FragmentCommentBinding;
import com.accountabilibuddies.accountabilibuddies.model.Comment;
import com.accountabilibuddies.accountabilibuddies.network.APIClient;
import com.accountabilibuddies.accountabilibuddies.util.ImageUtils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommentsFragment extends Fragment {

    private FragmentCommentBinding binding;
    private CommentsAdapter mAdapter;
    private APIClient client;
    private ArrayList<Comment> mCommentList;
    private LinearLayoutManager layoutManager;

    private EditText etComment;

    static final String TAG = CommentsFragment.class.getSimpleName();
    static final String POST_ID = "postId";

    public CommentsFragment() {
    }

    public static CommentsFragment getInstance(String postId) {
        CommentsFragment frag = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString(POST_ID, postId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment,container,false);

        client = APIClient.getClient();

        mCommentList = new ArrayList<>();
        mAdapter = new CommentsAdapter(getContext(), mCommentList);
        binding.rVComments.setAdapter(mAdapter);
        binding.rVComments.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rVComments.addItemDecoration(itemDecoration);

        layoutManager = new LinearLayoutManager(getContext());
        binding.rVComments.setLayoutManager(layoutManager);

        setUpNewCommentListener();

        //Fetch Comments
        getComments();
        return binding.getRoot();
    }

    private void setUpNewCommentListener() {

        ImageView ivAvatar = (ImageView) binding.lNewComment.findViewById(R.id.ivAvatar);

        ParseUser user = ParseUser.getCurrentUser();
        String profilePhotoUrl = (String) user.get("profilePhotoUrl");

        ImageUtils.loadCircularProfileImage(
                getContext(),
                profilePhotoUrl,
                ivAvatar
        );

        ImageButton ibComment = (ImageButton) binding.lNewComment.findViewById(R.id.ibComment);
        etComment = (EditText) binding.lNewComment.findViewById(R.id.etComment);

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==0) {
                    ibComment.setImageResource(R.drawable.send_false);
                } else if(charSequence.length()>0) {
                    ibComment.setImageResource(R.drawable.send);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        ibComment.setOnClickListener(
                (View v) -> {
                    String comment = etComment.getText().toString();
                    if(comment.length()>0) {
                        postComment(comment);
                    }
                }
        );

        etComment.requestFocus();
    }

    private void getComments() {
        client.getCommentList(getArguments().getString(POST_ID), new APIClient.GetCommentsListListener() {

            @Override
            public void onSuccess(List<Comment> commentsList) {
                if (commentsList != null) {
                    mCommentList.clear();
                    Collections.reverse(commentsList);
                    mCommentList.addAll(commentsList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String error_message) {

            }
        });
    }

    public void postComment(String postComment) {
        Comment comment = new Comment();

        comment.setText(postComment);
        comment.setUser(ParseApplication.getCurrentUser());
        APIClient.getClient().addComment(getArguments().getString(POST_ID), comment,
            new APIClient.PostListener() {
                @Override
                public void onSuccess() {
                    etComment.setText("");
                    mCommentList.add(0,comment);
                    mAdapter.notifyItemInserted(0);
                    layoutManager.scrollToPosition(0);
                }

                @Override
                public void onFailure(String error_message) {
                    //TODO: handle failure
                }
            });
    }
}
