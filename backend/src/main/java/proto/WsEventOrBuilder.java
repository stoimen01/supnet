// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ws.proto

package proto;

public interface WsEventOrBuilder extends
    // @@protoc_insertion_point(interface_extends:WsEvent)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>optional .InvitationEvent invitation_event = 1;</code>
   */
  proto.InvitationEvent getInvitationEvent();
  /**
   * <code>optional .InvitationEvent invitation_event = 1;</code>
   */
  proto.InvitationEventOrBuilder getInvitationEventOrBuilder();

  public proto.WsEvent.EventCase getEventCase();
}