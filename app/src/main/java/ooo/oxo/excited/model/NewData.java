package ooo.oxo.excited.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by seasonyuu on 2016/12/8.
 */

public class NewData {

    public List<Channel> channels;
    public List<Card> cards;
    public List<Card> timeline;
    public User user;
    public VoteCard voteCard;
    @SerializedName("setFollowState")
    public Follow followState;
    public Card card;
    public PreviewCard previewCard;
    @SerializedName("addWebCard")
    public AddCard webCard;
    @SerializedName("addImageCard")
    public AddCard imageCard;

}
