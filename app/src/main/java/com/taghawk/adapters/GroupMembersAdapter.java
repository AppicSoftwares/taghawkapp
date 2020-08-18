package com.taghawk.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.RowChatMessagesListBinding;
import com.taghawk.databinding.RowGroupMemberBinding;
import com.taghawk.model.RemoveFirebaseListenerModel;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.MemberModel;
import com.taghawk.model.login.LoginFirebaseModel;
import com.taghawk.model.request.User;
import com.taghawk.util.AppUtils;
import com.taghawk.util.TimeAgo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class GroupMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private PositionedLinkedHashmap<String,MemberModel> membersHashmap, completeMembersHashmap;
    private OnClickListener onClickListener;
    private SearchListener searchListener;
    private HashMap<String, LoginFirebaseModel> usersHashMap;
    public HashMap<String, RemoveFirebaseListenerModel> listenerModelHashMap;

    public GroupMembersAdapter(PositionedLinkedHashmap<String,MemberModel> membersHasmap, SearchListener searchListener, OnClickListener onClickListener) {
        this.membersHashmap = membersHasmap;
        this.completeMembersHashmap = membersHashmap;
        this.onClickListener = onClickListener;
        this.searchListener = searchListener;
        this.usersHashMap = new HashMap<>();
        listenerModelHashMap = new HashMap<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RowGroupMemberBinding mBinding = RowGroupMemberBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new MemberViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((MemberViewHolder) viewHolder).bind(membersHashmap.get(membersHashmap.getKeyValue(position)));
    }

    @Override
    public int getItemCount() {
        return membersHashmap.size();
    }

    public interface OnClickListener {
        void onClick(MemberModel memberModel, View view);
    }

    ;

    private class MemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RowGroupMemberBinding viewBinding;

        public MemberViewHolder(RowGroupMemberBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.llGroupMember.setOnClickListener(this);
            viewBinding.ivMember.setOnClickListener(this);
            viewBinding.tvMemberName.setOnClickListener(this);
        }

        public void bind(final MemberModel memberModel) {
            if (memberModel.getMemberType()==AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_SUPER_ADMIN)
            {
                AppUtils.loadCircularImage(itemView.getContext(), memberModel.getMemberImage(), 300, R.drawable.ic_detail_user_placeholder, viewBinding.ivMember, true);
                viewBinding.tvMemberName.setText(memberModel.getMemberName());
            }
            else if (usersHashMap.containsKey(memberModel.getMemberId()))
             setMemberData(memberModel);
            else {
                Query query = DataManager.getInstance().getUserNodeQuery(memberModel.getMemberId());
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                            if (loginFirebaseModel!=null) {
                                usersHashMap.put(memberModel.getMemberId(), loginFirebaseModel);
                                setMemberData(memberModel);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                query.addValueEventListener(valueEventListener);
                RemoveFirebaseListenerModel removeFirebaseListenerModel = new RemoveFirebaseListenerModel();
                removeFirebaseListenerModel.setValueEventListener(valueEventListener);
                removeFirebaseListenerModel.setQuery(query);
                listenerModelHashMap.put(memberModel.getMemberId(), removeFirebaseListenerModel);
            }
            switch (memberModel.getMemberType()) {
                case AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_SUPER_ADMIN:
                case AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_OWNER:
                    viewBinding.llOwnerType.setVisibility(View.VISIBLE);
                    viewBinding.ivOwnerIcon.setVisibility(View.VISIBLE);
                    viewBinding.tvMemberType.setText(itemView.getContext().getString(R.string.owner));
                    break;
                case AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_ADMIN:
                    viewBinding.llOwnerType.setVisibility(View.VISIBLE);
                    viewBinding.ivOwnerIcon.setVisibility(View.GONE);
                    viewBinding.tvMemberType.setText(itemView.getContext().getString(R.string.admin));
                    break;
                case AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_MEMBER:
                    viewBinding.llOwnerType.setVisibility(View.GONE);
                    break;
            }
        }

        /**
         * used to set the member's data
         * @param memberModel model class for the current member
         */
        private void setMemberData(MemberModel memberModel)
        {
            final User user = DataManager.getInstance().getUserDetails();
            LoginFirebaseModel loginFirebaseModel = usersHashMap.get(memberModel.getMemberId());
            memberModel.setMemberImage(loginFirebaseModel.getProfilePicture());
            memberModel.setMemberName(loginFirebaseModel.getFullName());
            AppUtils.loadCircularImage(itemView.getContext(), memberModel.getMemberImage(), 300, R.drawable.ic_detail_user_placeholder, viewBinding.ivMember, true);
            viewBinding.tvMemberName.setText(memberModel.getMemberName());
            if (memberModel.getMemberId().equalsIgnoreCase(user.getUserId())) {
                viewBinding.tvMemberName.append(AppUtils.getSpannableString(itemView.getContext(), " (" + itemView.getContext().getString(R.string.you) + ")", R.color.txt_light_gray, 1f, false, false, false, null));
            } else
                viewBinding.tvMemberName.append("");
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_member_name:
                case R.id.iv_member:
                case R.id.ll_group_member:
                    onClickListener.onClick(membersHashmap.get(membersHashmap.getKeyValue(getAdapterPosition())), view);
                    break;
            }
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                FilterResults filterResults = new FilterResults();
                if (charString.isEmpty()) {
                    filterResults.values = completeMembersHashmap;
                } else {
                    PositionedLinkedHashmap<String, MemberModel> hashMap = new PositionedLinkedHashmap<>();
                    for (final Map.Entry<String, MemberModel> entry : completeMembersHashmap.entrySet()) {
                        MemberModel memberModel = entry.getValue();
                        if (memberModel.getMemberName().toLowerCase().contains(charString.toLowerCase())) {
                            hashMap.put(memberModel.getMemberId(), memberModel);
                        }
                    }
                    filterResults.values = hashMap;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                membersHashmap = (PositionedLinkedHashmap<String, MemberModel>) filterResults.values;
                membersHashmap.updateIndexes();
                notifyDataSetChanged();
                searchListener.onSearch(membersHashmap.size());
            }
        };
    }

    public interface SearchListener {
        void onSearch(int searchCount);
    }
}
