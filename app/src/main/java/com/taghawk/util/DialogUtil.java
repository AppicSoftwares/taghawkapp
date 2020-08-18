package com.taghawk.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.taghawk.R;
import com.taghawk.adapters.ClusterAdapter;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.BottomSheetCheckoutverifyBinding;
import com.taghawk.databinding.BottomSheetDenyFeedbackBinding;
import com.taghawk.databinding.BottomSheetDialogBinding;
import com.taghawk.databinding.BottomSheetDialogDeleteGroupBinding;
import com.taghawk.databinding.BottomSheetDialogEditVerificationTypeBinding;
import com.taghawk.databinding.BottomSheetDialogInviteCodeBinding;
import com.taghawk.databinding.BottomSheetDialogKnowMoreBinding;
import com.taghawk.databinding.BottomSheetDialogRatingBinding;
import com.taghawk.databinding.BottomSheetDialogShareInChatBinding;
import com.taghawk.databinding.BottomSheetDialogUnfollowBinding;
import com.taghawk.databinding.BottomSheetDialogWalletKnowMoreBinding;
import com.taghawk.databinding.BottomSheetFeedbackBinding;
import com.taghawk.databinding.BottomSheetPaymentSelectionMethodBinding;
import com.taghawk.databinding.DialogCommonBinding;
import com.taghawk.databinding.GuestUserBottomSheetDialogBinding;
import com.taghawk.databinding.LayoutClusterViewBinding;
import com.taghawk.databinding.ShippingTypeSelectionBottomSheetDialogBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.OnDialogItemObjectClickListener;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.tag.ClusterBean;
import com.taghawk.ui.onboard.login.LoginActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by appinventiv on 15/2/18.
 */

public class DialogUtil {

    private static final String TAG = "DialogUtil";
    private static DialogUtil INSTANCE;
    private ProgressDialog progDialog;
    int shippigType = 0;
    private boolean pickupDisable, deliverDisable, shippingDisable;

    private DialogUtil() {

    }

    public static DialogUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (DialogUtil.class) {
                if (INSTANCE == null)
                    INSTANCE = new DialogUtil();
            }
        }
        return INSTANCE;
    }


    /**
     * method to show alert dialog in app
     * This dialog appears everywhere in the app , either with title or not, either with two btns or one
     */
//    public void showAlertDialog(Context context, @Nullable String title, @Nullable String message, @Nullable String positiveLabel, @Nullable String negativeLabel,
//                                @Nullable final OnDialogItemClickListener onDialogItemClickListener) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        if (message != null && message.length() > 0)
//            builder.setMessage(message);
//        if (title != null && title.length() > 0)
//            builder.setTitle(title);
//        if (positiveLabel != null && positiveLabel.length() > 0) {
//            builder.setPositiveButton(positiveLabel, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    if (onDialogItemClickListener != null)
//                        onDialogItemClickListener.onPositiveBtnClick();
//
//                    dialogInterface.dismiss();
//                }
//            });
//        }
//        if (negativeLabel != null && negativeLabel.length() > 0) {
//            builder.setNegativeButton(negativeLabel, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    if (onDialogItemClickListener != null)
//                        onDialogItemClickListener.onNegativeBtnClick();
//
//                    dialogInterface.dismiss();
//                }
//            });
//        }
//
//        builder.setCancelable(false);
//        AlertDialog alertDialog = builder.create();
//        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationZoom;
//        alertDialog.show();
//    }

    /**
     * method to show cancelable alert dialog in app
     * This dialog appears everywhere in the app , either with title or not, either with two btns or one
     */
    public void showCancelableAlertDialog(Context context, @Nullable String title, @Nullable String message, @Nullable String positiveLabel, @Nullable String negativeLabel,
                                          @Nullable final OnDialogItemClickListener onDialogItemClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (message != null && message.length() > 0)
            builder.setMessage(message);
        if (title != null && title.length() > 0)
            builder.setTitle(title);
        if (positiveLabel != null && positiveLabel.length() > 0) {
            builder.setPositiveButton(positiveLabel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (onDialogItemClickListener != null)
                        onDialogItemClickListener.onPositiveBtnClick();

                    dialogInterface.dismiss();
                }
            });
        }
        if (negativeLabel != null && negativeLabel.length() > 0) {
            builder.setNegativeButton(negativeLabel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (onDialogItemClickListener != null)
                        onDialogItemClickListener.onNegativeBtnClick();

                    dialogInterface.dismiss();
                }
            });
        }
        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
//        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationZoom;
        alertDialog.show();
    }


    /**
     * method to show alert dialog in app
     * This dialog appears everywhere in the app , either with title or not, either with two btns or one
     */
//    public void showAppDialog(Context context, @Nullable String message, @Nullable String positiveLabel, @Nullable String negativeLabel, int logo
//            , boolean cancelable, @Nullable final OnDialogItemClickListener onDialogItemClickListener) {
//        final Dialog dialog = new Dialog(context);
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_logout);
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(null);
//        dialog.setCancelable(cancelable);
//        TextView tvMessage = dialog.findViewById(R.id.tv_message_failure);
//        TextView btnYes = dialog.findViewById(R.id.btn_yes);
//        TextView btnNo = dialog.findViewById(R.id.btn_no);
//        ImageView ivIcon = dialog.findViewById(R.id.iv_icon);
//
//        if (logo == 0) {
//            ivIcon.setVisibility(View.GONE);
//        } else {
//            ivIcon.setVisibility(View.VISIBLE);
//            ivIcon.setImageResource(logo);
//        }
//
//        if (message != null && message.length() > 0) {
//            tvMessage.setText(message);
//        } else {
//            tvMessage.setVisibility(View.GONE);
//        }
//
//        if (positiveLabel != null && positiveLabel.length() > 0) {
//            btnYes.setText(positiveLabel);
//        } else {
//            btnYes.setVisibility(View.GONE);
//        }
//
//        if (negativeLabel != null && negativeLabel.length() > 0) {
//            btnNo.setText(negativeLabel);
//
//        } else {
//            btnNo.setVisibility(View.GONE);
//
//        }
//
//        btnYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onDialogItemClickListener != null)
//                    onDialogItemClickListener.onPositiveBtnClick();
//
//                dialog.dismiss();
//            }
//        });
//
//        btnNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onDialogItemClickListener != null)
//                    onDialogItemClickListener.onNegativeBtnClick();
//
//                dialog.dismiss();
//            }
//        });
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationZoom;
//        dialog.show();
//    }
    public void CustomBottomSheetDialog(final Context context, String title, final String message, final OnDialogItemClickListener listener) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        BottomSheetDialogBinding binding = BottomSheetDialogBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        if (message.equalsIgnoreCase(context.getString(R.string.stripe))) {
            binding.tvMessage.setText(getSpannableText(context));
        } else {
            binding.tvMessage.setText(message);
        }
        binding.tvTitle.setText(title);
        binding.cardOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPositiveBtnClick();
                if (message != null && !message.equalsIgnoreCase(context.getResources().getString(R.string.update_msg)))
                    dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void CustomBottomSheetJustifiedDialog(final Context context, String title, final String message, final OnDialogItemClickListener listener) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        BottomSheetDialogBinding binding = BottomSheetDialogBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.tvMessage.setVisibility(View.GONE);
        binding.tvMessageJustified.setVisibility(View.VISIBLE);
        if (message.equalsIgnoreCase(context.getString(R.string.stripe))) {
            binding.tvMessageJustified.setText(getSpannableText(context));
        } else {
            binding.tvMessageJustified.setText(message);
        }
        binding.tvTitle.setText(title);
        binding.cardOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPositiveBtnClick();
                if (message != null && !message.equalsIgnoreCase(context.getResources().getString(R.string.update_msg)))
                    dialog.dismiss();
            }
        });
        dialog.show();
    }

    private Spannable getSpannableText(Context context) {
        String str = context.getString(R.string.account_pending_status_msg);
        Spannable spannable = new SpannableStringBuilder(str);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "galano_grotesque_bold.otf");
        spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 45, 52, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#6772e4")), 45, 52, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.2f), 45, 52, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
        spannable.setSpan(font, 45, 52, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
        return spannable;
    }


    public void CustomBottomSheetDialogForDeleteGroup(final Context context, String title, final OnDialogItemClickListener listener) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        final BottomSheetDialogDeleteGroupBinding binding = BottomSheetDialogDeleteGroupBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.tvTitle.setText(TextUtils.concat(context.getString(R.string.delete_tag), "!"));
        binding.tvSubtitle.setText(title);
        binding.tvCode.setText(AppUtils.generateRandomAlphanumericSting());
        binding.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etCode.getText().toString().trim().equalsIgnoreCase(""))
                    Toast.makeText(context, context.getString(R.string.enter_code_first), Toast.LENGTH_SHORT).show();
                else if (!binding.etCode.getText().toString().trim().equals(binding.tvCode.getText()))
                    Toast.makeText(context, context.getString(R.string.enter_valid_code), Toast.LENGTH_SHORT).show();
                else if (!AppUtils.isConnection(context))
                    Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                else {
                    listener.onPositiveBtnClick();
                    dialog.dismiss();
                }
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNegativeBtnClick();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void CustomBottomSheetDialogForVerificationType(final Context context, final int type, String initialText, final OnDialogItemObjectClickListener listener) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        final BottomSheetDialogEditVerificationTypeBinding binding = BottomSheetDialogEditVerificationTypeBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        switch (type) {
            case 0:
                binding.tvTitle.setText(TextUtils.concat(context.getString(R.string.edit), " " + context.getString(R.string.hint_tag_name)));
                binding.tvSubtitle.setText(context.getString(R.string.please_enter_new_tag_name));
                binding.etVerificationData.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.etVerificationData.setHint(context.getString(R.string.enter_tag_name));
                break;
            case AppConstants.TAG_VERIFICATION_METHOD.EMAIL:
                binding.tvTitle.setText(TextUtils.concat(context.getString(R.string.edit), " " + context.getString(R.string.email_edit)));
                binding.tvSubtitle.setText(context.getString(R.string.please_enter_new_verification_email));
                binding.etVerificationData.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.etVerificationData.setHint(context.getString(R.string.enter_email_domain_after));
                break;
            case AppConstants.TAG_VERIFICATION_METHOD.PASSWORD:
                binding.tvTitle.setText(TextUtils.concat(context.getString(R.string.edit), " " + context.getString(R.string.option_password)));
                binding.tvSubtitle.setText(context.getString(R.string.please_enter_new_verification_password));
                binding.etVerificationData.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.etVerificationData.setHint(context.getString(R.string.enter_password_to_join));
                break;
            case 4:
                binding.tvTitle.setText(TextUtils.concat(context.getString(R.string.edit), " " + context.getString(R.string.announcement)));
                binding.tvSubtitle.setText(context.getString(R.string.please_enter_new_announcement));
                binding.etVerificationData.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.etVerificationData.setHint(context.getString(R.string.enter_announcement));
                break;
            case 5:
                binding.tvTitle.setText(TextUtils.concat(context.getString(R.string.edit), " " + context.getString(R.string.description)));
                binding.tvSubtitle.setText(context.getString(R.string.please_enter_new_description));
                binding.etVerificationData.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.etVerificationData.setHint(context.getString(R.string.enter_description));
                break;
        }
        binding.etVerificationData.setText(initialText);
        binding.etVerificationData.setSelection(binding.etVerificationData.getText().toString().trim().length());
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        binding.tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastText = "";
                switch (type) {
                    case 0:
                        toastText = context.getString(R.string.enter_tag_name_first);
                        break;
                    case AppConstants.TAG_VERIFICATION_METHOD.EMAIL:
                        toastText = context.getString(R.string.enter_domain_first);
                        break;
                    case AppConstants.TAG_VERIFICATION_METHOD.PASSWORD:
                        toastText = context.getString(R.string.enter_password_first);
                        break;
                    case 4:
                        toastText = context.getString(R.string.enter_announcement_first);
                        break;
                }
                if (binding.etVerificationData.getText().toString().trim().equalsIgnoreCase(""))
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                else {
                    listener.onPositiveBtnClick(binding.etVerificationData.getText().toString().trim());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void CustomBottomSheetDialogShareInChat(Context context, final OnDialogItemObjectClickListener onClickListener) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        BottomSheetDialogShareInChatBinding binding = BottomSheetDialogShareInChatBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        ((View) binding.llMain.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        View.OnClickListener onClickListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onPositiveBtnClick(view);
                dialog.dismiss();
            }
        };
        binding.tvTakePhoto.setOnClickListener(onClickListener1);
        binding.tvGallery.setOnClickListener(onClickListener1);
        binding.tvShareCommunity.setOnClickListener(onClickListener1);
        binding.tvShareProduct.setOnClickListener(onClickListener1);
        dialog.show();
    }

    public void CustomCommonBottomSheetDialog(Context context, String title, String msg, String okButton, String cancelButton, final DialogCallback dialogCallback) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        DialogCommonBinding binding = DialogCommonBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.tvMessage.setText(msg);
        binding.tvTitle.setText(title);
        if (title.length() > 0)
            binding.tvTitle.setText(title);
        else {
            binding.tvTitle.setVisibility(View.GONE);
        }
        binding.tvCancel.setText(cancelButton);
        binding.tvShare.setText(okButton);
        binding.tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogCallback.submit("");
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogCallback.cancel();
            }
        });
        dialog.show();
    }

    public void CustomShippingTypeBottomSheetDialog(Context context, Integer[] shipping, final OnDialogViewClickListener listener) {
        shippigType = 0;
        deliverDisable = false;
        shippingDisable = false;
        pickupDisable = false;
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        final ShippingTypeSelectionBottomSheetDialogBinding binding = ShippingTypeSelectionBottomSheetDialogBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        if (shipping.length < 3) {
            if (!Arrays.asList(shipping).contains(1)) {
                pickupDisable = true;
                binding.tvPickup.setEnabled(false);
                binding.tvPickup.setTextColor(context.getResources().getColor(R.color.txt_light_gray));
                binding.tvPickup.setPaintFlags(binding.tvPickup.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else if (!Arrays.asList(shipping).contains(2)) {
                deliverDisable = true;
                binding.tvDeliver.setEnabled(false);
                binding.tvDeliver.setTextColor(context.getResources().getColor(R.color.txt_light_gray));
                binding.tvDeliver.setPaintFlags(binding.tvPickup.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else if (!Arrays.asList(shipping).contains(3)) {
                binding.tvShipping.setEnabled(false);
                shippingDisable = false;
                binding.tvShipping.setTextColor(context.getResources().getColor(R.color.txt_light_gray));
                binding.tvShipping.setPaintFlags(binding.tvPickup.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
        binding.tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shippigType > 0) {
                    dialog.dismiss();
                }
            }
        });
        binding.tvPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                setShippingAvaliability(v.getContext(), binding, R.color.White, R.drawable.edit_field_filled_drawable, R.color.txt_black, R.drawable.edit_field_drawable, R.color.txt_black, R.drawable.edit_field_drawable);
                shippigType = 1;
                listener.onSubmit("", shippigType);
                dialog.dismiss();

            }
        });
        binding.tvDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shippigType = 2;
//                setShippingAvaliability(v.getContext(), binding, R.color.txt_black, R.drawable.edit_field_drawable, R.color.White, R.drawable.edit_field_filled_drawable, R.color.txt_black, R.drawable.edit_field_drawable);
                listener.onSubmit("", shippigType);
                dialog.dismiss();

            }
        });
        binding.tvShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shippigType = 3;
//                setShippingAvaliability(v.getContext(), binding, R.color.txt_black, R.drawable.edit_field_drawable, R.color.txt_black, R.drawable.edit_field_drawable, R.color.White, R.drawable.edit_field_filled_drawable);
                listener.onSubmit("", shippigType);
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    public void customRewardsOrPaymentDialog(Context context, final OnDialogViewClickListener listener) {

        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        final BottomSheetPaymentSelectionMethodBinding binding = BottomSheetPaymentSelectionMethodBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.tvRewardsPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSubmit("", 1);
                dialog.dismiss();
            }
        });
        binding.tvGPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSubmit("", 2);
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void setShippingAvaliability(Context mActiviy, ShippingTypeSelectionBottomSheetDialogBinding mBinding, int p, int p2, int p3, int p4, int p5, int p6) {
        if (pickupDisable) {
            mBinding.tvPickup.setTextColor(mActiviy.getResources().getColor(R.color.txt_light_gray));
            mBinding.tvPickup.setBackgroundDrawable(mActiviy.getResources().getDrawable(R.drawable.edit_field_drawable));
        } else {
            mBinding.tvPickup.setTextColor(mActiviy.getResources().getColor(p));
            mBinding.tvPickup.setBackgroundDrawable(mActiviy.getResources().getDrawable(p2));
        }
        if (deliverDisable) {
            mBinding.tvDeliver.setTextColor(mActiviy.getResources().getColor(R.color.txt_light_gray));
            mBinding.tvDeliver.setBackgroundDrawable(mActiviy.getResources().getDrawable(R.drawable.edit_field_drawable));
        } else {
            mBinding.tvDeliver.setTextColor(mActiviy.getResources().getColor(p3));
            mBinding.tvDeliver.setBackgroundDrawable(mActiviy.getResources().getDrawable(p4));
        }
        if (shippingDisable) {
            mBinding.tvShipping.setTextColor(mActiviy.getResources().getColor(R.color.txt_light_gray));
            mBinding.tvShipping.setBackgroundDrawable(mActiviy.getResources().getDrawable(R.drawable.edit_field_drawable));
        } else {
            mBinding.tvShipping.setTextColor(mActiviy.getResources().getColor(p5));
            mBinding.tvShipping.setBackgroundDrawable(mActiviy.getResources().getDrawable(p6));

        }
    }


    public void CustomTagBottomSheetDialog(Context context, ArrayList<ClusterBean> arrayListCluster) {

        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        LayoutClusterViewBinding binding = LayoutClusterViewBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        setupList(context, binding, arrayListCluster);
//        binding.ivCross.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
        dialog.show();
    }

    private void setupList(Context context, LayoutClusterViewBinding binding, ArrayList<ClusterBean> arrayListCluster) {
        binding.rvCluster.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ClusterAdapter clusterAdapter = new ClusterAdapter(context, arrayListCluster);
        binding.rvCluster.setAdapter(clusterAdapter);
    }

    /*
     * show required pemission dialog
     * */
    public void showPermissionsRequiredDialog(final Activity activity) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity);

        builder.setTitle(R.string.permisssion_required)
                .setMessage(activity.getResources().getString(R.string.permission_req_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AppUtils.openAppSettings(activity);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void CustomGuestUserBottomSheetDialog(final Context context, final BaseActivity activity) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        GuestUserBottomSheetDialogBinding binding = GuestUserBottomSheetDialogBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null)
                    setupViewModel(context, activity, true, dialog);
                else {
                    DataManager.getInstance().clearPreferences();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    ((BaseActivity) context).finish();
                }

            }
        });
        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null)
                    setupViewModel(context, activity, true, dialog);
                else {
                    DataManager.getInstance().clearPreferences();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    ((BaseActivity) context).finish();
                }
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setupViewModel(final Context context, final BaseActivity activity, final boolean isSignup, final BottomSheetDialog dialog) {
        final CommonViewModel commonViewModel = ViewModelProviders.of(activity).get(CommonViewModel.class);
        commonViewModel.setGenericListeners(((BaseActivity) context).getErrorObserver(), ((BaseActivity) context).getFailureResponseObserver(), ((BaseActivity) context).getLoadingStateObserver());

        commonViewModel.logout().observe(activity, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse productListingModel) {
                activity.getLoadingStateObserver().onChanged(false);
                if (productListingModel != null && productListingModel.getCode() == 200) {
//                    if (DataManager.getInstance() != null)
//                        DataManager.getInstance().clearPreferences();
//                    else DataManager.getInstance().saveAccessToken("");
                    if (dialog != null) {
                        dialog.dismiss();
                    }
//                    ((BaseActivity) context).finishAffinity();
//                    if (isSignup) {
//                        Intent intent = new Intent(context, LoginActivity.class);
//                        intent.putExtra(AppConstants.IS_SIGN_UP, true);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        context.startActivity(intent);
//                        ((BaseActivity) context).finish();
//                    } else {
//                        Intent intent = new Intent(context, LoginActivity.class);
//                        intent.putExtra(AppConstants.IS_LOGIN, true);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        context.startActivity(intent);
//                        ((BaseActivity) context).finish();
//                    }
                    DataManager.getInstance().clearPreferences();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    ((BaseActivity) context).finish();
                }
            }
        });
        commonViewModel.hitLogOut(activity.getDeviceId());

    }

    public void CustomUnFollowRemoveBottomSheetDialog(Context context, String message, String name, String actionButton, String imgUrl, boolean isRemove, boolean isBlock, final OnDialogItemClickListener listener) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        BottomSheetDialogUnfollowBinding binding = BottomSheetDialogUnfollowBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        if (imgUrl != null && imgUrl.length() > 0) {
            Glide.with(context).asBitmap().load(imgUrl).apply(RequestOptions.placeholderOf(R.drawable.ic_detail_user_placeholder)).into(binding.ivUserImg);
        }
        if (isBlock) {
            Spannable spannable = new SpannableString(name);
            spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvMsg.setText(spannable);
            binding.tvMsg.append(" ");
            binding.tvMsg.append(message);
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.tvTitle.setText(context.getString(R.string.block));
        } else {
            if (!isRemove) {
                Spannable spannable = new SpannableString(message + " " + name);
                spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), message.length() + 1, message.length() + name.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.tvMsg.setText(spannable, TextView.BufferType.SPANNABLE);
            } else {
                Spannable spannable = new SpannableString(context.getString(R.string.remove_title) + " " + name + " " + message);
                spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), context.getString(R.string.remove_title).length() + 1, context.getString(R.string.remove_title).length() + 1 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.tvMsg.setText(spannable, TextView.BufferType.SPANNABLE);
                binding.tvTitle.setVisibility(View.VISIBLE);
                binding.tvTitle.setText(context.getString(R.string.remove_follower));
            }
        }
//        binding.tvMsg.setText(message);
        binding.tvUnfollow.setText(actionButton);
        binding.tvUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onPositiveBtnClick();
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void CustomGiveRatingBottomSheetDialog(Context context, String name, String productName, String imgUrl, final OnDialogViewClickListener listener) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        final BottomSheetDialogRatingBinding binding = BottomSheetDialogRatingBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        if (imgUrl != null && imgUrl.length() > 0) {
            Glide.with(context).asBitmap().load(imgUrl).apply(RequestOptions.placeholderOf(R.drawable.ic_detail_user_placeholder)).into(binding.ivUserImg);
        }

        binding.tvProductName.setText(productName);
        binding.tvName.setText(name);
        binding.tvTitle.setVisibility(View.VISIBLE);
        binding.tvTitle.setText(context.getString(R.string.rate_the_sellet));

        binding.tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding != null && binding.rating.getRating() > 0) {
                    dialog.dismiss();
                    listener.onSubmit(binding.etProductDescription.getText().toString().trim(), (int) binding.rating.getRating());
                }
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void CustomRateBottomSheetDialog(Context context, String sellerName, String productName, final OnDialogItemClickListener listener) {

        if (context != null) {
            final BottomSheetDialog dialog = new BottomSheetDialog(context);
            BottomSheetFeedbackBinding binding = BottomSheetFeedbackBinding.inflate(LayoutInflater.from(context));
            dialog.setContentView(binding.getRoot());
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                    .getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            try {
                ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Spannable spannable = new SpannableString(context.getString(R.string.would_you_like_to_rate) + " " + sellerName + " " + context.getString(R.string.for_product) + " " + productName);
            spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), context.getString(R.string.would_you_like_to_rate).length(), context.getString(R.string.would_you_like_to_rate).length() + 1 + sellerName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), context.getString(R.string.would_you_like_to_rate).length() + sellerName.length() + 3 + context.getString(R.string.for_product).length(), context.getString(R.string.would_you_like_to_rate).length() + sellerName.length() + context.getString(R.string.for_product).length() + 3 + productName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvMsg.setText(spannable, TextView.BufferType.SPANNABLE);
            binding.tvRate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    listener.onPositiveBtnClick();
                }
            });
            binding.ivCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNegativeBtnClick();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public void CustomDenyBottomSheetDialog(Context context, final OnDialogItemClickListener listener) {

        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        BottomSheetDenyFeedbackBinding binding = BottomSheetDenyFeedbackBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.tvLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNegativeBtnClick();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void CustomBottomSheetCheckoutDialog(Context context, final OnDialogItemClickListener listener) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        BottomSheetCheckoutverifyBinding binding = BottomSheetCheckoutverifyBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

        binding.tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPositiveBtnClick();
                dialog.dismiss();
            }
        });
        binding.tvCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNegativeBtnClick();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void customBottomSheetDialogKnowMore(Context context) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        BottomSheetDialogKnowMoreBinding binding = BottomSheetDialogKnowMoreBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void customBottomSheetRefundDialog(Context context, String str) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        BottomSheetDialogKnowMoreBinding binding = BottomSheetDialogKnowMoreBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        binding.tvTitle1.setVisibility(View.GONE);
        binding.tvSecond.setVisibility(View.GONE);
        binding.tvTitle.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvFirstMsg.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
        } else {
            binding.tvFirstMsg.setText(Html.fromHtml(str));
        }
//        binding.tvFirstMsg.setText("" + str);
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

     public void customBottomSheetWalletDialog(Context context, String str) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
         BottomSheetDialogWalletKnowMoreBinding binding = BottomSheetDialogWalletKnowMoreBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        binding.tvTitle1.setVisibility(View.GONE);
        binding.tvSecond.setVisibility(View.GONE);
        binding.tvTitle.setVisibility(View.VISIBLE);
        binding.tvTitle.setText("Cash Out Policy");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            binding.tvFirstMsg.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
//        } else {
//            binding.tvFirstMsg.setText(Html.fromHtml(str));
//        }
         binding.tvFirstMsg.setText(str);
//        binding.tvFirstMsg.setLineSpacing(1.5f, 1);
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void customInviteCodeDialog(Context context, String str, final OnDialogViewClickListener onDialogViewClickListener) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        final BottomSheetDialogInviteCodeBinding binding = BottomSheetDialogInviteCodeBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etInvitationCode.getText().toString().trim().length() > 0) {
                    onDialogViewClickListener.onSubmit(binding.etInvitationCode.getText().toString(), 1);
                    dialog.dismiss();
                } else {
                    onDialogViewClickListener.onSubmit("", 2);
                }

            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogViewClickListener.onSubmit("", 3);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
