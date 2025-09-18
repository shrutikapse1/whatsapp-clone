package com.alibou.whatsappclone.chat;

import com.alibou.whatsappclone.message.Message;
import com.alibou.whatsappclone.message.MessageState;
import com.alibou.whatsappclone.message.MessageType;
import com.alibou.whatsappclone.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="chat")
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID,
            query="SELECT DISTINCT c from Chat WHERE c.sender.id=:senderId OR c.recipient_id=:senderId ORDER BY createdDate DESC")
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER,
            query="SELECT DISTINCT c FROM Chat WHERE(c.sender.id=:senderId AND c.recipient.id=:recipientId) OR (c.sender.id=:recipientId AND c.recipient.id=:senderId)")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("createdDate DESC")
    private List<Message> messages;

    @Transient
    public String getChatName(final String senderId){
        return recipient.getId().equals(senderId) ? sender.getFirstName() + " " + sender.getLastName() : recipient.getFirstName() + " " + recipient.getLastName();
    }

    @Transient
    public long getUnreadMessages(final String senderId){
        return messages.stream()
                .filter(m->m.getReceiverId().equals(senderId))
                .filter(m-> MessageState.SENT == m.getState())
                .count();
    }

    @Transient
    public String getLastMessage(){
        if(messages!=null && !messages.isEmpty()){
            if(messages.get(0).getType()!= MessageType.TEXT){
                return "Attachment";
            }
            return messages.get(0).getContent();
        }
        return null;
    }

    @Transient
    public LocalDateTime getLastMessageTime(){
        if(messages!=null && !messages.isEmpty()){
            return messages.get(0).getCreatedDate();
        }
        return null;
    }




}
