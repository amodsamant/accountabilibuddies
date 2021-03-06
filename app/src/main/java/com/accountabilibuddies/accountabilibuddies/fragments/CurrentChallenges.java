package com.accountabilibuddies.accountabilibuddies.fragments;


import android.content.Intent;

import com.accountabilibuddies.accountabilibuddies.activity.ChallengeDetailsActivity;
import com.accountabilibuddies.accountabilibuddies.activity.ChallengeOneOnOneActivity;
import com.accountabilibuddies.accountabilibuddies.application.ParseApplication;
import com.accountabilibuddies.accountabilibuddies.model.Challenge;
import com.accountabilibuddies.accountabilibuddies.util.Constants;

public class CurrentChallenges extends ChallengesFragment {
    @Override
    protected void openChallenge(int position) {
        if (mChallengeList.isEmpty())
            return;

        Challenge challenge = mChallengeList.get(position);

        Intent intent;
        if(challenge.getType()== Constants.TYPE_ONE_ON_ONE) {
            intent = new Intent(getActivity(), ChallengeOneOnOneActivity.class);
        } else {
            intent = new Intent(getActivity(), ChallengeDetailsActivity.class);
        }
        intent.putExtra("challengeId", challenge.getObjectId());
        intent.putExtra("name", challenge.getName());
        getActivity().startActivity(intent);
    }

    @Override
    protected void getChallenges() {
        mAdapter.setChallengeType(1);
        client.getCurrentChallengeList(ParseApplication.getCurrentUser(), listener);
    }
}
