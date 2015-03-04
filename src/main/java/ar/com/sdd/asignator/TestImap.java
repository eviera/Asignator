package ar.com.sdd.asignator;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

public class TestImap {

	public static void doit() throws MessagingException, IOException {
		Folder folder = null;
		Store store = null;
		try {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");

			Session session = Session.getDefaultInstance(props, null);
			// session.setDebug(true);
			store = session.getStore("imaps");
			store.connect("imap.gmail.com", "eviera.sdd@gmail.com", "telperion");
			folder = store.getFolder("[Gmail]/Drafts");
			/*
			 * Others GMail folders : [Gmail]/All Mail This folder contains all
			 * of your Gmail messages. [Gmail]/Drafts Your drafts. [Gmail]/Sent
			 * Mail Messages you sent to other people. [Gmail]/Spam Messages
			 * marked as spam. [Gmail]/Starred Starred messages. [Gmail]/Trash
			 * Messages deleted from Gmail.
			 */
			folder.open(Folder.READ_WRITE);
			Message messages[] = folder.getMessages();
			System.out.println("No of Messages : " + folder.getMessageCount());
			System.out.println("No of Unread Messages : " + folder.getUnreadMessageCount());
			for (int i = 0; i < messages.length; ++i) {
				System.out.println("MESSAGE #" + (i + 1) + ":");
				Message msg = messages[i];
				/*
				 * if we don''t want to fetch messages already processed if
				 * (!msg.isSet(Flags.Flag.SEEN)) { String from = "unknown"; ...
				 * }
				 */
				String from = "unknown";
				if (msg.getReplyTo().length >= 1) {
					from = msg.getReplyTo()[0].toString();
				} else if (msg.getFrom().length >= 1) {
					from = msg.getFrom()[0].toString();
				}
				String subject = msg.getSubject();
				System.out.println("Saving ... " + subject + " " + from);
				// you may want to replace the spaces with "_"
				// the TEMP directory is used to store the files
				String filename = "c:/temp/" + subject;
				//saveParts(msg.getContent(), filename);
				msg.setFlag(Flags.Flag.SEEN, true);
				// to delete the message
				// msg.setFlag(Flags.Flag.DELETED, true);
				MimeMessage copia = new MimeMessage((MimeMessage)msg);
				copia.setSubject("Copia de:" + msg.getSubject() );
				copia.setFlag(Flags.Flag.SEEN, false);
				folder.appendMessages(new Message[]{copia}); 
				copia.saveChanges();
				msg.setFlag(Flags.Flag.DELETED,true); 
				folder.expunge();

			}
		} finally {
			if (folder != null) {
				folder.close(true);
			}
			if (store != null) {
				store.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			TestImap.doit();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
