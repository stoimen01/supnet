// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: signaling.proto

package proto;

/**
 * Protobuf type {@code SignalingIntent}
 */
public  final class SignalingIntent extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:SignalingIntent)
    SignalingIntentOrBuilder {
  // Use SignalingIntent.newBuilder() to construct.
  private SignalingIntent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private SignalingIntent() {
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private SignalingIntent(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            proto.CreateRoomIntent.Builder subBuilder = null;
            if (intentCase_ == 1) {
              subBuilder = ((proto.CreateRoomIntent) intent_).toBuilder();
            }
            intent_ =
                input.readMessage(proto.CreateRoomIntent.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((proto.CreateRoomIntent) intent_);
              intent_ = subBuilder.buildPartial();
            }
            intentCase_ = 1;
            break;
          }
          case 18: {
            proto.JoinRoomIntent.Builder subBuilder = null;
            if (intentCase_ == 2) {
              subBuilder = ((proto.JoinRoomIntent) intent_).toBuilder();
            }
            intent_ =
                input.readMessage(proto.JoinRoomIntent.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((proto.JoinRoomIntent) intent_);
              intent_ = subBuilder.buildPartial();
            }
            intentCase_ = 2;
            break;
          }
          case 26: {
            proto.LeaveRoomIntent.Builder subBuilder = null;
            if (intentCase_ == 3) {
              subBuilder = ((proto.LeaveRoomIntent) intent_).toBuilder();
            }
            intent_ =
                input.readMessage(proto.LeaveRoomIntent.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((proto.LeaveRoomIntent) intent_);
              intent_ = subBuilder.buildPartial();
            }
            intentCase_ = 3;
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return proto.Signaling.internal_static_SignalingIntent_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return proto.Signaling.internal_static_SignalingIntent_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            proto.SignalingIntent.class, proto.SignalingIntent.Builder.class);
  }

  private int intentCase_ = 0;
  private java.lang.Object intent_;
  public enum IntentCase
      implements com.google.protobuf.Internal.EnumLite {
    CREATE_ROOM(1),
    JOIN_ROOM(2),
    LEAVE_ROOM(3),
    INTENT_NOT_SET(0);
    private final int value;
    private IntentCase(int value) {
      this.value = value;
    }
    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static IntentCase valueOf(int value) {
      return forNumber(value);
    }

    public static IntentCase forNumber(int value) {
      switch (value) {
        case 1: return CREATE_ROOM;
        case 2: return JOIN_ROOM;
        case 3: return LEAVE_ROOM;
        case 0: return INTENT_NOT_SET;
        default: return null;
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  public IntentCase
  getIntentCase() {
    return IntentCase.forNumber(
        intentCase_);
  }

  public static final int CREATE_ROOM_FIELD_NUMBER = 1;
  /**
   * <code>optional .CreateRoomIntent create_room = 1;</code>
   */
  public proto.CreateRoomIntent getCreateRoom() {
    if (intentCase_ == 1) {
       return (proto.CreateRoomIntent) intent_;
    }
    return proto.CreateRoomIntent.getDefaultInstance();
  }
  /**
   * <code>optional .CreateRoomIntent create_room = 1;</code>
   */
  public proto.CreateRoomIntentOrBuilder getCreateRoomOrBuilder() {
    if (intentCase_ == 1) {
       return (proto.CreateRoomIntent) intent_;
    }
    return proto.CreateRoomIntent.getDefaultInstance();
  }

  public static final int JOIN_ROOM_FIELD_NUMBER = 2;
  /**
   * <code>optional .JoinRoomIntent join_room = 2;</code>
   */
  public proto.JoinRoomIntent getJoinRoom() {
    if (intentCase_ == 2) {
       return (proto.JoinRoomIntent) intent_;
    }
    return proto.JoinRoomIntent.getDefaultInstance();
  }
  /**
   * <code>optional .JoinRoomIntent join_room = 2;</code>
   */
  public proto.JoinRoomIntentOrBuilder getJoinRoomOrBuilder() {
    if (intentCase_ == 2) {
       return (proto.JoinRoomIntent) intent_;
    }
    return proto.JoinRoomIntent.getDefaultInstance();
  }

  public static final int LEAVE_ROOM_FIELD_NUMBER = 3;
  /**
   * <code>optional .LeaveRoomIntent leave_room = 3;</code>
   */
  public proto.LeaveRoomIntent getLeaveRoom() {
    if (intentCase_ == 3) {
       return (proto.LeaveRoomIntent) intent_;
    }
    return proto.LeaveRoomIntent.getDefaultInstance();
  }
  /**
   * <code>optional .LeaveRoomIntent leave_room = 3;</code>
   */
  public proto.LeaveRoomIntentOrBuilder getLeaveRoomOrBuilder() {
    if (intentCase_ == 3) {
       return (proto.LeaveRoomIntent) intent_;
    }
    return proto.LeaveRoomIntent.getDefaultInstance();
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (intentCase_ == 1) {
      output.writeMessage(1, (proto.CreateRoomIntent) intent_);
    }
    if (intentCase_ == 2) {
      output.writeMessage(2, (proto.JoinRoomIntent) intent_);
    }
    if (intentCase_ == 3) {
      output.writeMessage(3, (proto.LeaveRoomIntent) intent_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (intentCase_ == 1) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, (proto.CreateRoomIntent) intent_);
    }
    if (intentCase_ == 2) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, (proto.JoinRoomIntent) intent_);
    }
    if (intentCase_ == 3) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(3, (proto.LeaveRoomIntent) intent_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof proto.SignalingIntent)) {
      return super.equals(obj);
    }
    proto.SignalingIntent other = (proto.SignalingIntent) obj;

    boolean result = true;
    result = result && getIntentCase().equals(
        other.getIntentCase());
    if (!result) return false;
    switch (intentCase_) {
      case 1:
        result = result && getCreateRoom()
            .equals(other.getCreateRoom());
        break;
      case 2:
        result = result && getJoinRoom()
            .equals(other.getJoinRoom());
        break;
      case 3:
        result = result && getLeaveRoom()
            .equals(other.getLeaveRoom());
        break;
      case 0:
      default:
    }
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptorForType().hashCode();
    switch (intentCase_) {
      case 1:
        hash = (37 * hash) + CREATE_ROOM_FIELD_NUMBER;
        hash = (53 * hash) + getCreateRoom().hashCode();
        break;
      case 2:
        hash = (37 * hash) + JOIN_ROOM_FIELD_NUMBER;
        hash = (53 * hash) + getJoinRoom().hashCode();
        break;
      case 3:
        hash = (37 * hash) + LEAVE_ROOM_FIELD_NUMBER;
        hash = (53 * hash) + getLeaveRoom().hashCode();
        break;
      case 0:
      default:
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static proto.SignalingIntent parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static proto.SignalingIntent parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static proto.SignalingIntent parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static proto.SignalingIntent parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static proto.SignalingIntent parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static proto.SignalingIntent parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static proto.SignalingIntent parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static proto.SignalingIntent parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static proto.SignalingIntent parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static proto.SignalingIntent parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(proto.SignalingIntent prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code SignalingIntent}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:SignalingIntent)
      proto.SignalingIntentOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return proto.Signaling.internal_static_SignalingIntent_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.Signaling.internal_static_SignalingIntent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.SignalingIntent.class, proto.SignalingIntent.Builder.class);
    }

    // Construct using proto.SignalingIntent.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      intentCase_ = 0;
      intent_ = null;
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return proto.Signaling.internal_static_SignalingIntent_descriptor;
    }

    public proto.SignalingIntent getDefaultInstanceForType() {
      return proto.SignalingIntent.getDefaultInstance();
    }

    public proto.SignalingIntent build() {
      proto.SignalingIntent result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public proto.SignalingIntent buildPartial() {
      proto.SignalingIntent result = new proto.SignalingIntent(this);
      if (intentCase_ == 1) {
        if (createRoomBuilder_ == null) {
          result.intent_ = intent_;
        } else {
          result.intent_ = createRoomBuilder_.build();
        }
      }
      if (intentCase_ == 2) {
        if (joinRoomBuilder_ == null) {
          result.intent_ = intent_;
        } else {
          result.intent_ = joinRoomBuilder_.build();
        }
      }
      if (intentCase_ == 3) {
        if (leaveRoomBuilder_ == null) {
          result.intent_ = intent_;
        } else {
          result.intent_ = leaveRoomBuilder_.build();
        }
      }
      result.intentCase_ = intentCase_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof proto.SignalingIntent) {
        return mergeFrom((proto.SignalingIntent)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(proto.SignalingIntent other) {
      if (other == proto.SignalingIntent.getDefaultInstance()) return this;
      switch (other.getIntentCase()) {
        case CREATE_ROOM: {
          mergeCreateRoom(other.getCreateRoom());
          break;
        }
        case JOIN_ROOM: {
          mergeJoinRoom(other.getJoinRoom());
          break;
        }
        case LEAVE_ROOM: {
          mergeLeaveRoom(other.getLeaveRoom());
          break;
        }
        case INTENT_NOT_SET: {
          break;
        }
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      proto.SignalingIntent parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (proto.SignalingIntent) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int intentCase_ = 0;
    private java.lang.Object intent_;
    public IntentCase
        getIntentCase() {
      return IntentCase.forNumber(
          intentCase_);
    }

    public Builder clearIntent() {
      intentCase_ = 0;
      intent_ = null;
      onChanged();
      return this;
    }


    private com.google.protobuf.SingleFieldBuilderV3<
        proto.CreateRoomIntent, proto.CreateRoomIntent.Builder, proto.CreateRoomIntentOrBuilder> createRoomBuilder_;
    /**
     * <code>optional .CreateRoomIntent create_room = 1;</code>
     */
    public proto.CreateRoomIntent getCreateRoom() {
      if (createRoomBuilder_ == null) {
        if (intentCase_ == 1) {
          return (proto.CreateRoomIntent) intent_;
        }
        return proto.CreateRoomIntent.getDefaultInstance();
      } else {
        if (intentCase_ == 1) {
          return createRoomBuilder_.getMessage();
        }
        return proto.CreateRoomIntent.getDefaultInstance();
      }
    }
    /**
     * <code>optional .CreateRoomIntent create_room = 1;</code>
     */
    public Builder setCreateRoom(proto.CreateRoomIntent value) {
      if (createRoomBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        intent_ = value;
        onChanged();
      } else {
        createRoomBuilder_.setMessage(value);
      }
      intentCase_ = 1;
      return this;
    }
    /**
     * <code>optional .CreateRoomIntent create_room = 1;</code>
     */
    public Builder setCreateRoom(
        proto.CreateRoomIntent.Builder builderForValue) {
      if (createRoomBuilder_ == null) {
        intent_ = builderForValue.build();
        onChanged();
      } else {
        createRoomBuilder_.setMessage(builderForValue.build());
      }
      intentCase_ = 1;
      return this;
    }
    /**
     * <code>optional .CreateRoomIntent create_room = 1;</code>
     */
    public Builder mergeCreateRoom(proto.CreateRoomIntent value) {
      if (createRoomBuilder_ == null) {
        if (intentCase_ == 1 &&
            intent_ != proto.CreateRoomIntent.getDefaultInstance()) {
          intent_ = proto.CreateRoomIntent.newBuilder((proto.CreateRoomIntent) intent_)
              .mergeFrom(value).buildPartial();
        } else {
          intent_ = value;
        }
        onChanged();
      } else {
        if (intentCase_ == 1) {
          createRoomBuilder_.mergeFrom(value);
        }
        createRoomBuilder_.setMessage(value);
      }
      intentCase_ = 1;
      return this;
    }
    /**
     * <code>optional .CreateRoomIntent create_room = 1;</code>
     */
    public Builder clearCreateRoom() {
      if (createRoomBuilder_ == null) {
        if (intentCase_ == 1) {
          intentCase_ = 0;
          intent_ = null;
          onChanged();
        }
      } else {
        if (intentCase_ == 1) {
          intentCase_ = 0;
          intent_ = null;
        }
        createRoomBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>optional .CreateRoomIntent create_room = 1;</code>
     */
    public proto.CreateRoomIntent.Builder getCreateRoomBuilder() {
      return getCreateRoomFieldBuilder().getBuilder();
    }
    /**
     * <code>optional .CreateRoomIntent create_room = 1;</code>
     */
    public proto.CreateRoomIntentOrBuilder getCreateRoomOrBuilder() {
      if ((intentCase_ == 1) && (createRoomBuilder_ != null)) {
        return createRoomBuilder_.getMessageOrBuilder();
      } else {
        if (intentCase_ == 1) {
          return (proto.CreateRoomIntent) intent_;
        }
        return proto.CreateRoomIntent.getDefaultInstance();
      }
    }
    /**
     * <code>optional .CreateRoomIntent create_room = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        proto.CreateRoomIntent, proto.CreateRoomIntent.Builder, proto.CreateRoomIntentOrBuilder> 
        getCreateRoomFieldBuilder() {
      if (createRoomBuilder_ == null) {
        if (!(intentCase_ == 1)) {
          intent_ = proto.CreateRoomIntent.getDefaultInstance();
        }
        createRoomBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            proto.CreateRoomIntent, proto.CreateRoomIntent.Builder, proto.CreateRoomIntentOrBuilder>(
                (proto.CreateRoomIntent) intent_,
                getParentForChildren(),
                isClean());
        intent_ = null;
      }
      intentCase_ = 1;
      onChanged();;
      return createRoomBuilder_;
    }

    private com.google.protobuf.SingleFieldBuilderV3<
        proto.JoinRoomIntent, proto.JoinRoomIntent.Builder, proto.JoinRoomIntentOrBuilder> joinRoomBuilder_;
    /**
     * <code>optional .JoinRoomIntent join_room = 2;</code>
     */
    public proto.JoinRoomIntent getJoinRoom() {
      if (joinRoomBuilder_ == null) {
        if (intentCase_ == 2) {
          return (proto.JoinRoomIntent) intent_;
        }
        return proto.JoinRoomIntent.getDefaultInstance();
      } else {
        if (intentCase_ == 2) {
          return joinRoomBuilder_.getMessage();
        }
        return proto.JoinRoomIntent.getDefaultInstance();
      }
    }
    /**
     * <code>optional .JoinRoomIntent join_room = 2;</code>
     */
    public Builder setJoinRoom(proto.JoinRoomIntent value) {
      if (joinRoomBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        intent_ = value;
        onChanged();
      } else {
        joinRoomBuilder_.setMessage(value);
      }
      intentCase_ = 2;
      return this;
    }
    /**
     * <code>optional .JoinRoomIntent join_room = 2;</code>
     */
    public Builder setJoinRoom(
        proto.JoinRoomIntent.Builder builderForValue) {
      if (joinRoomBuilder_ == null) {
        intent_ = builderForValue.build();
        onChanged();
      } else {
        joinRoomBuilder_.setMessage(builderForValue.build());
      }
      intentCase_ = 2;
      return this;
    }
    /**
     * <code>optional .JoinRoomIntent join_room = 2;</code>
     */
    public Builder mergeJoinRoom(proto.JoinRoomIntent value) {
      if (joinRoomBuilder_ == null) {
        if (intentCase_ == 2 &&
            intent_ != proto.JoinRoomIntent.getDefaultInstance()) {
          intent_ = proto.JoinRoomIntent.newBuilder((proto.JoinRoomIntent) intent_)
              .mergeFrom(value).buildPartial();
        } else {
          intent_ = value;
        }
        onChanged();
      } else {
        if (intentCase_ == 2) {
          joinRoomBuilder_.mergeFrom(value);
        }
        joinRoomBuilder_.setMessage(value);
      }
      intentCase_ = 2;
      return this;
    }
    /**
     * <code>optional .JoinRoomIntent join_room = 2;</code>
     */
    public Builder clearJoinRoom() {
      if (joinRoomBuilder_ == null) {
        if (intentCase_ == 2) {
          intentCase_ = 0;
          intent_ = null;
          onChanged();
        }
      } else {
        if (intentCase_ == 2) {
          intentCase_ = 0;
          intent_ = null;
        }
        joinRoomBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>optional .JoinRoomIntent join_room = 2;</code>
     */
    public proto.JoinRoomIntent.Builder getJoinRoomBuilder() {
      return getJoinRoomFieldBuilder().getBuilder();
    }
    /**
     * <code>optional .JoinRoomIntent join_room = 2;</code>
     */
    public proto.JoinRoomIntentOrBuilder getJoinRoomOrBuilder() {
      if ((intentCase_ == 2) && (joinRoomBuilder_ != null)) {
        return joinRoomBuilder_.getMessageOrBuilder();
      } else {
        if (intentCase_ == 2) {
          return (proto.JoinRoomIntent) intent_;
        }
        return proto.JoinRoomIntent.getDefaultInstance();
      }
    }
    /**
     * <code>optional .JoinRoomIntent join_room = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        proto.JoinRoomIntent, proto.JoinRoomIntent.Builder, proto.JoinRoomIntentOrBuilder> 
        getJoinRoomFieldBuilder() {
      if (joinRoomBuilder_ == null) {
        if (!(intentCase_ == 2)) {
          intent_ = proto.JoinRoomIntent.getDefaultInstance();
        }
        joinRoomBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            proto.JoinRoomIntent, proto.JoinRoomIntent.Builder, proto.JoinRoomIntentOrBuilder>(
                (proto.JoinRoomIntent) intent_,
                getParentForChildren(),
                isClean());
        intent_ = null;
      }
      intentCase_ = 2;
      onChanged();;
      return joinRoomBuilder_;
    }

    private com.google.protobuf.SingleFieldBuilderV3<
        proto.LeaveRoomIntent, proto.LeaveRoomIntent.Builder, proto.LeaveRoomIntentOrBuilder> leaveRoomBuilder_;
    /**
     * <code>optional .LeaveRoomIntent leave_room = 3;</code>
     */
    public proto.LeaveRoomIntent getLeaveRoom() {
      if (leaveRoomBuilder_ == null) {
        if (intentCase_ == 3) {
          return (proto.LeaveRoomIntent) intent_;
        }
        return proto.LeaveRoomIntent.getDefaultInstance();
      } else {
        if (intentCase_ == 3) {
          return leaveRoomBuilder_.getMessage();
        }
        return proto.LeaveRoomIntent.getDefaultInstance();
      }
    }
    /**
     * <code>optional .LeaveRoomIntent leave_room = 3;</code>
     */
    public Builder setLeaveRoom(proto.LeaveRoomIntent value) {
      if (leaveRoomBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        intent_ = value;
        onChanged();
      } else {
        leaveRoomBuilder_.setMessage(value);
      }
      intentCase_ = 3;
      return this;
    }
    /**
     * <code>optional .LeaveRoomIntent leave_room = 3;</code>
     */
    public Builder setLeaveRoom(
        proto.LeaveRoomIntent.Builder builderForValue) {
      if (leaveRoomBuilder_ == null) {
        intent_ = builderForValue.build();
        onChanged();
      } else {
        leaveRoomBuilder_.setMessage(builderForValue.build());
      }
      intentCase_ = 3;
      return this;
    }
    /**
     * <code>optional .LeaveRoomIntent leave_room = 3;</code>
     */
    public Builder mergeLeaveRoom(proto.LeaveRoomIntent value) {
      if (leaveRoomBuilder_ == null) {
        if (intentCase_ == 3 &&
            intent_ != proto.LeaveRoomIntent.getDefaultInstance()) {
          intent_ = proto.LeaveRoomIntent.newBuilder((proto.LeaveRoomIntent) intent_)
              .mergeFrom(value).buildPartial();
        } else {
          intent_ = value;
        }
        onChanged();
      } else {
        if (intentCase_ == 3) {
          leaveRoomBuilder_.mergeFrom(value);
        }
        leaveRoomBuilder_.setMessage(value);
      }
      intentCase_ = 3;
      return this;
    }
    /**
     * <code>optional .LeaveRoomIntent leave_room = 3;</code>
     */
    public Builder clearLeaveRoom() {
      if (leaveRoomBuilder_ == null) {
        if (intentCase_ == 3) {
          intentCase_ = 0;
          intent_ = null;
          onChanged();
        }
      } else {
        if (intentCase_ == 3) {
          intentCase_ = 0;
          intent_ = null;
        }
        leaveRoomBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>optional .LeaveRoomIntent leave_room = 3;</code>
     */
    public proto.LeaveRoomIntent.Builder getLeaveRoomBuilder() {
      return getLeaveRoomFieldBuilder().getBuilder();
    }
    /**
     * <code>optional .LeaveRoomIntent leave_room = 3;</code>
     */
    public proto.LeaveRoomIntentOrBuilder getLeaveRoomOrBuilder() {
      if ((intentCase_ == 3) && (leaveRoomBuilder_ != null)) {
        return leaveRoomBuilder_.getMessageOrBuilder();
      } else {
        if (intentCase_ == 3) {
          return (proto.LeaveRoomIntent) intent_;
        }
        return proto.LeaveRoomIntent.getDefaultInstance();
      }
    }
    /**
     * <code>optional .LeaveRoomIntent leave_room = 3;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        proto.LeaveRoomIntent, proto.LeaveRoomIntent.Builder, proto.LeaveRoomIntentOrBuilder> 
        getLeaveRoomFieldBuilder() {
      if (leaveRoomBuilder_ == null) {
        if (!(intentCase_ == 3)) {
          intent_ = proto.LeaveRoomIntent.getDefaultInstance();
        }
        leaveRoomBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            proto.LeaveRoomIntent, proto.LeaveRoomIntent.Builder, proto.LeaveRoomIntentOrBuilder>(
                (proto.LeaveRoomIntent) intent_,
                getParentForChildren(),
                isClean());
        intent_ = null;
      }
      intentCase_ = 3;
      onChanged();;
      return leaveRoomBuilder_;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:SignalingIntent)
  }

  // @@protoc_insertion_point(class_scope:SignalingIntent)
  private static final proto.SignalingIntent DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new proto.SignalingIntent();
  }

  public static proto.SignalingIntent getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SignalingIntent>
      PARSER = new com.google.protobuf.AbstractParser<SignalingIntent>() {
    public SignalingIntent parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new SignalingIntent(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<SignalingIntent> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SignalingIntent> getParserForType() {
    return PARSER;
  }

  public proto.SignalingIntent getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

