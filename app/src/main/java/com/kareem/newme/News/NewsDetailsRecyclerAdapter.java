package com.kareem.newme.News;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.kareem.newme.Chatting.Messages.Message;
import com.kareem.newme.Connections.VolleyRequest;
import com.kareem.newme.Constants;
import com.kareem.newme.Model.Comment;
import com.kareem.newme.Model.Like;
import com.kareem.newme.Model.News;
import com.kareem.newme.R;
import com.kareem.newme.RunTimeItems;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Message} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class NewsDetailsRecyclerAdapter extends RecyclerView.Adapter<NewsDetailsRecyclerAdapter.ViewHolder> {

    private News news;
    private Context context;
    private String newsId;
    private Boolean isLiked = false;
    private int likeId;

    public NewsDetailsRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType ==0)
         view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_details_list_item, parent, false);
        else view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comments_list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        else return 1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //TODO dont forget the image
        if (position == 0) setNewsLayout(holder.mView);
        else setCommentsLayout(holder.mView, position - 1);
    }


    private void setCommentsLayout(View v, final int position) {
        final EditText content = (EditText) v.findViewById(R.id.comments_list_content);
        TextView name = (TextView) v.findViewById(R.id.comments_list_name);
        final Comment comment = news.getComments().get(position);
        content.setText(comment.getContent());
        content.setEnabled(false);
        name.setText(comment.getUserName());
        final View edit_button = v.findViewById(R.id.comments_list_edit_button);
        View delete_button = v.findViewById(R.id.comments_list_del_button);

        if (RunTimeItems.loggedUser != null
                &&
                (RunTimeItems.loggedUser.getUserType().equals(Constants.ADMIN_TYPE)
                        || String.valueOf(comment.getUserId()).equals(RunTimeItems.loggedUser.getId()))) {

            edit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (content.isEnabled()) {
                        content.setEnabled(false);
                        editComment(position, content.getText().toString());
                    } else {
                        content.setEnabled(true);
                        content.requestFocus();
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(content, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            });
            content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        content.setEnabled(false);

                    }
                }
            });
            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(position);
                }
            });

        } else {
            edit_button.setVisibility(View.GONE);
            delete_button.setVisibility(View.GONE);
        }
        //TODO
        // 1- set listeners
        //idk just it seems i forgot something :)
    }

    private void editComment(int position, String newContent) {
        Comment comment = news.getComments().get(position);
        comment.setContent(newContent);
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("req", "editComment");
        stringMap.put("newId", newsId);
        stringMap.put("commentId", position + "");
        stringMap.put("comment", new Gson().toJson(comment));
        VolleyRequest volleyRequest = new VolleyRequest(Constants.BASE_URL, stringMap, context) {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(String response) {

            }
        };
        volleyRequest.start();
    }

    private void delete(int position) {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("req", "deleteComment");
        stringMap.put("newId", newsId);
        stringMap.put("commentId", position + "");
        VolleyRequest volleyRequest = new VolleyRequest(Constants.BASE_URL, stringMap, context) {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(String response) {

            }
        };
        volleyRequest.start();
    }

    private void setNewsLayout(View view) {
        TextView newsTitle = (TextView) view.findViewById(R.id.news_details_textView_title);
        TextView newsDetails = (TextView) view.findViewById(R.id.news_details_textView_content);
        ImageView imageView = (ImageView) view.findViewById(R.id.news_details_image_view);
        // Set their text
        newsTitle.setText(news.getTitle());
        newsDetails.setText(news.getContent());
        Picasso.with(context).load(news.getImage_url()).error(R.mipmap.default_image_news).into(imageView);
        setNewsButtonsLayoutAndListeners(view);
    }

    private void setNewsButtonsLayoutAndListeners(View view) {
        //like button
        //edit
        //delete
        ImageView likeButton = (ImageView) view.findViewById(R.id.news_details_image_view_like);

        if (RunTimeItems.loggedUser != null) {
            isLiked = false;
            for (int i = 0; i < news.getLikes().size(); i++) {
                Like like = news.getLikes().get(i);
                if (RunTimeItems.loggedUser.getId().equals(like.getUserId().toString())) {
                    isLiked = true;
                    likeId = i;
                    break;
                }

            }
        } else likeButton.setVisibility(View.GONE);
        if (isLiked)
            likeButton.setImageResource(R.drawable.like_active);
        else likeButton.setImageResource(R.drawable.like);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLikeRequest();
            }
        });
        //delete button
        ImageView delete_button = (ImageView) view.findViewById(R.id.news_details_image_view_delete);
        setDeleteButton(delete_button);

        //edit button
        ImageView editButton = (ImageView) view.findViewById(R.id.news_details_image_view_edit);
        setEditButton(editButton);
    }

    private void sendLikeRequest() {
        Map<String, String> stringMap = new HashMap<>();
        if (isLiked) stringMap.put("req", "dislikeNew");
        else
            stringMap.put("req", "likeNew");
        stringMap.put("newId", newsId);
        //TODO fix this
        if (isLiked) stringMap.put("likeId", String.valueOf(likeId));
        else
            stringMap.put("like", new Gson().toJson(new Like(Integer.valueOf(RunTimeItems.loggedUser.getId()))));
        VolleyRequest volleyRequest = new VolleyRequest(Constants.BASE_URL, stringMap, context) {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(String response) {

            }
        };
        volleyRequest.start();
    }

    private void setDeleteButton(View deleteButton) {
        if (RunTimeItems.loggedUser != null && RunTimeItems.loggedUser.getUserType().equals(Constants.ADMIN_TYPE))
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> stringMap = new HashMap<>();
                    stringMap.put("req", "deleteNew");
                    stringMap.put("newId", newsId);
                    VolleyRequest volleyRequest = new VolleyRequest(Constants.BASE_URL, stringMap, context) {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }

                        @Override
                        public void onResponse(String response) {
                        }
                    };
                    volleyRequest.start();
                }
            });
        else deleteButton.setVisibility(View.GONE);
    }

    private void setEditButton(View editButton) {
        if (RunTimeItems.loggedUser != null && RunTimeItems.loggedUser.getUserType().equals(Constants.ADMIN_TYPE))
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewsEditor.class);
                    intent.putExtra(Constants.NEWS_DATA, new Gson().toJson(news));
                    intent.putExtra(Constants.NEWS_ID, newsId);
                    context.startActivity(intent);
                }
            });
        else editButton.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return news.getComments().size() + 1;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsId() {
        return newsId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public  View mView;
        public final TextView mContentView;
        public News mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
