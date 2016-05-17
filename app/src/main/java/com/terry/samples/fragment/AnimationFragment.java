package com.terry.samples.fragment;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.terry.samples.R;
import com.terry.samples.activity.MainActivity;

/**
 * Created by terry on 2016/5/16.
 */
public class AnimationFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private View view1, view2, view3;
    private ViewGroup view4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animation, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view1 = view.findViewById(R.id.image1);
        view2 = view.findViewById(R.id.image2);
        view3 = view.findViewById(R.id.image3);
        view4 = (ViewGroup) view.findViewById(R.id.layout);
        // enable here or add "android:animateLayoutChanges="true" in xml
        view4.setLayoutTransition(new LayoutTransition());

        view1.setOnClickListener(this);
        view2.setOnClickListener(this);
        view3.setOnClickListener(this);
        view4.setOnClickListener(this);
    }

    @Override
    protected void setUpActionBar() {
        MainActivity activity = (MainActivity) getActivity();
        activity.setCustomToolbarTitle("AnimationTest");
        activity.setToolbarExpanded(false);
    }

    @Override
    public void onClick(View v) {
        AnimatorSet animSet;
        switch (v.getId()) {
            case R.id.image1:
                /**
                 * Property name: translationX, translationY, rotation, rotationX, rotationY,
                 * scaleX, scaleY, x, y and alpha
                 */
                ObjectAnimator.ofFloat(view1, "alpha", 0f, 1f)
                        .setDuration(1000)
                        .start();
                break;
            case R.id.image2:
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setTarget(view2);
                animator.setDuration(1000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        view2.setAlpha((Float) animation.getAnimatedValue());
                    }
                });
                animator.start();
                break;
            case R.id.image3:
                /**
                 *  Set AnimatorSet programing
                 */
//                Animator anim1 = AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_out);
//                Animator anim2 = AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_in);
//                anim1.setTarget(view3);
//                anim2.setTarget(view3);
//                animSet = new AnimatorSet();
//                animSet.play(anim1).before(anim2);
//                animSet.setDuration(1000);
//                animSet.start();

                /**
                 *  Set animatorSet from animator resource
                 */
                animSet =
                        (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_out_in);
                animSet.setTarget(view3);
                animSet.start();
                break;
            case R.id.layout:
                final Button button = new Button(getActivity());
                button.setText("aa");
                view4.addView(button);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        view4.removeView(button);
                    }
                });
                break;
        }
    }
}
