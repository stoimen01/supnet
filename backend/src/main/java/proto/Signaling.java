// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: signaling.proto

package proto;

public final class Signaling {
  private Signaling() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_User_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_User_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Room_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Room_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_CreateRoomIntent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_CreateRoomIntent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_JoinRoomIntent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_JoinRoomIntent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_LeaveRoomIntent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_LeaveRoomIntent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SendMessageIntent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_SendMessageIntent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SignalingIntent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_SignalingIntent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RoomsInfoEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RoomsInfoEvent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RoomCreatedEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RoomCreatedEvent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RoomRemovedEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RoomRemovedEvent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RoomNotCreatedEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RoomNotCreatedEvent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RoomJoinedEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RoomJoinedEvent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RoomNotJoinedEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RoomNotJoinedEvent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RoomLeavedEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RoomLeavedEvent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RoomNotLeavedEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RoomNotLeavedEvent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_MessageReceived_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_MessageReceived_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_MessageSendEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_MessageSendEvent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_MessageNotSendEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_MessageNotSendEvent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SignalingEvent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_SignalingEvent_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017signaling.proto\" \n\004User\022\n\n\002id\030\001 \001(\t\022\014\n" +
      "\004name\030\002 \001(\t\"8\n\004Room\022\n\n\002id\030\001 \001(\t\022\014\n\004name\030" +
      "\002 \001(\t\022\026\n\007members\030\003 \003(\0132\005.User\"%\n\020CreateR" +
      "oomIntent\022\021\n\troom_name\030\001 \001(\t\"!\n\016JoinRoom" +
      "Intent\022\017\n\007room_id\030\001 \001(\t\"\"\n\017LeaveRoomInte" +
      "nt\022\017\n\007room_id\030\001 \001(\t\"!\n\021SendMessageIntent" +
      "\022\014\n\004data\030\001 \001(\t\"\277\001\n\017SignalingIntent\022(\n\013cr" +
      "eate_room\030\001 \001(\0132\021.CreateRoomIntentH\000\022$\n\t" +
      "join_room\030\002 \001(\0132\017.JoinRoomIntentH\000\022&\n\nle" +
      "ave_room\030\003 \001(\0132\020.LeaveRoomIntentH\000\022*\n\014se",
      "nd_message\030\004 \001(\0132\022.SendMessageIntentH\000B\010" +
      "\n\006intent\"%\n\016RoomsInfoEvent\022\023\n\004list\030\001 \003(\013" +
      "2\005.Room\",\n\020RoomCreatedEvent\022\n\n\002id\030\001 \001(\t\022" +
      "\014\n\004name\030\002 \001(\t\"\036\n\020RoomRemovedEvent\022\n\n\002id\030" +
      "\001 \001(\t\"\025\n\023RoomNotCreatedEvent\"\035\n\017RoomJoin" +
      "edEvent\022\n\n\002id\030\001 \001(\t\"\024\n\022RoomNotJoinedEven" +
      "t\"\035\n\017RoomLeavedEvent\022\n\n\002id\030\001 \001(\t\"\024\n\022Room" +
      "NotLeavedEvent\"4\n\017MessageReceived\022\023\n\013sen" +
      "der_name\030\001 \001(\t\022\014\n\004data\030\002 \001(\t\"\022\n\020MessageS" +
      "endEvent\"\025\n\023MessageNotSendEvent\"\205\004\n\016Sign",
      "alingEvent\022%\n\nrooms_info\030\001 \001(\0132\017.RoomsIn" +
      "foEventH\000\022)\n\014room_created\030\002 \001(\0132\021.RoomCr" +
      "eatedEventH\000\0220\n\020room_not_created\030\003 \001(\0132\024" +
      ".RoomNotCreatedEventH\000\022\'\n\013room_joined\030\004 " +
      "\001(\0132\020.RoomJoinedEventH\000\022.\n\017room_not_join" +
      "ed\030\005 \001(\0132\023.RoomNotJoinedEventH\000\022\'\n\013room_" +
      "leaved\030\006 \001(\0132\020.RoomLeavedEventH\000\022.\n\017room" +
      "_not_leaved\030\007 \001(\0132\023.RoomNotLeavedEventH\000" +
      "\022)\n\014room_removed\030\010 \001(\0132\021.RoomRemovedEven" +
      "tH\000\022,\n\020message_received\030\t \001(\0132\020.MessageR",
      "eceivedH\000\022)\n\014message_send\030\n \001(\0132\021.Messag" +
      "eSendEventH\000\0220\n\020message_not_send\030\013 \001(\0132\024" +
      ".MessageNotSendEventH\000B\007\n\005eventB\t\n\005proto" +
      "P\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_User_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_User_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_User_descriptor,
        new java.lang.String[] { "Id", "Name", });
    internal_static_Room_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_Room_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Room_descriptor,
        new java.lang.String[] { "Id", "Name", "Members", });
    internal_static_CreateRoomIntent_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_CreateRoomIntent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_CreateRoomIntent_descriptor,
        new java.lang.String[] { "RoomName", });
    internal_static_JoinRoomIntent_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_JoinRoomIntent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_JoinRoomIntent_descriptor,
        new java.lang.String[] { "RoomId", });
    internal_static_LeaveRoomIntent_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_LeaveRoomIntent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_LeaveRoomIntent_descriptor,
        new java.lang.String[] { "RoomId", });
    internal_static_SendMessageIntent_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_SendMessageIntent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_SendMessageIntent_descriptor,
        new java.lang.String[] { "Data", });
    internal_static_SignalingIntent_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_SignalingIntent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_SignalingIntent_descriptor,
        new java.lang.String[] { "CreateRoom", "JoinRoom", "LeaveRoom", "SendMessage", "Intent", });
    internal_static_RoomsInfoEvent_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_RoomsInfoEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RoomsInfoEvent_descriptor,
        new java.lang.String[] { "List", });
    internal_static_RoomCreatedEvent_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_RoomCreatedEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RoomCreatedEvent_descriptor,
        new java.lang.String[] { "Id", "Name", });
    internal_static_RoomRemovedEvent_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_RoomRemovedEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RoomRemovedEvent_descriptor,
        new java.lang.String[] { "Id", });
    internal_static_RoomNotCreatedEvent_descriptor =
      getDescriptor().getMessageTypes().get(10);
    internal_static_RoomNotCreatedEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RoomNotCreatedEvent_descriptor,
        new java.lang.String[] { });
    internal_static_RoomJoinedEvent_descriptor =
      getDescriptor().getMessageTypes().get(11);
    internal_static_RoomJoinedEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RoomJoinedEvent_descriptor,
        new java.lang.String[] { "Id", });
    internal_static_RoomNotJoinedEvent_descriptor =
      getDescriptor().getMessageTypes().get(12);
    internal_static_RoomNotJoinedEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RoomNotJoinedEvent_descriptor,
        new java.lang.String[] { });
    internal_static_RoomLeavedEvent_descriptor =
      getDescriptor().getMessageTypes().get(13);
    internal_static_RoomLeavedEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RoomLeavedEvent_descriptor,
        new java.lang.String[] { "Id", });
    internal_static_RoomNotLeavedEvent_descriptor =
      getDescriptor().getMessageTypes().get(14);
    internal_static_RoomNotLeavedEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RoomNotLeavedEvent_descriptor,
        new java.lang.String[] { });
    internal_static_MessageReceived_descriptor =
      getDescriptor().getMessageTypes().get(15);
    internal_static_MessageReceived_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_MessageReceived_descriptor,
        new java.lang.String[] { "SenderName", "Data", });
    internal_static_MessageSendEvent_descriptor =
      getDescriptor().getMessageTypes().get(16);
    internal_static_MessageSendEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_MessageSendEvent_descriptor,
        new java.lang.String[] { });
    internal_static_MessageNotSendEvent_descriptor =
      getDescriptor().getMessageTypes().get(17);
    internal_static_MessageNotSendEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_MessageNotSendEvent_descriptor,
        new java.lang.String[] { });
    internal_static_SignalingEvent_descriptor =
      getDescriptor().getMessageTypes().get(18);
    internal_static_SignalingEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_SignalingEvent_descriptor,
        new java.lang.String[] { "RoomsInfo", "RoomCreated", "RoomNotCreated", "RoomJoined", "RoomNotJoined", "RoomLeaved", "RoomNotLeaved", "RoomRemoved", "MessageReceived", "MessageSend", "MessageNotSend", "Event", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
