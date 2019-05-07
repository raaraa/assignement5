package com.example.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class FragementArticle extends Fragment implements Serializable {
    public static final FragementArticle newInstance(Article article){
        FragementArticle fragementArticle = new FragementArticle();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable("article", article);
        fragementArticle.setArguments(bundle);
        return fragementArticle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Article article;
        if (savedInstanceState == null) {
            article = (Article) getArguments().getSerializable("article");
        }
        else {
            article = (Article) savedInstanceState.getSerializable("article");
        }
        View view = inflater.inflate(R.layout.frag_article, container, false);

        TextView title = view.findViewById(R.id.title);
        TextView author = view.findViewById(R.id.author);
        TextView date =  view.findViewById(R.id.date);
        TextView description =  view.findViewById(R.id.description);
        TextView page_count = view.findViewById(R.id.page_count);
        final ImageButton imageButton =  view.findViewById(R.id.image);

        title.setText(article.getTitle());
        author.setText(article.getAuthor());
        date.setText(article.getPublishedAt().split("T")[0]);
        description.setText(article.getDescription());
        page_count.setText(""+article.getIdx()+" of "+ "10");

        if (article.getUrlToImage() != null){
            Picasso picasso = new Picasso.Builder(view.getContext()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    final String changedUrl = article.getUrlToImage().replace("http:", "https:");
                    picasso.load(changedUrl) .error(R.drawable.no_image)
                            .placeholder(R.drawable.no_image) .into(imageButton);
                }
            }).build();
            picasso.load(article.getUrlToImage()) .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image) .into(imageButton);
        } else {
            Picasso.get().load(article.getUrlToImage()) .error(R.drawable.no_image).placeholder(R.drawable.no_image);
        }

        final Intent intent = new Intent((Intent.ACTION_VIEW));
        intent.setData(Uri.parse(article.getUrl()));


        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("article", getArguments().getSerializable("article"));
    }
}
