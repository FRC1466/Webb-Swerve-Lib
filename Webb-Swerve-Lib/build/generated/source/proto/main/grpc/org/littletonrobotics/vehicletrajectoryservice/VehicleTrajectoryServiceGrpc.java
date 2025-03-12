package org.littletonrobotics.vehicletrajectoryservice;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.61.0)",
    comments = "Source: VehicleTrajectoryService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class VehicleTrajectoryServiceGrpc {

  private VehicleTrajectoryServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest,
      org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse> getGenerateTrajectoryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GenerateTrajectory",
      requestType = org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest.class,
      responseType = org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest,
      org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse> getGenerateTrajectoryMethod() {
    io.grpc.MethodDescriptor<org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest, org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse> getGenerateTrajectoryMethod;
    if ((getGenerateTrajectoryMethod = VehicleTrajectoryServiceGrpc.getGenerateTrajectoryMethod) == null) {
      synchronized (VehicleTrajectoryServiceGrpc.class) {
        if ((getGenerateTrajectoryMethod = VehicleTrajectoryServiceGrpc.getGenerateTrajectoryMethod) == null) {
          VehicleTrajectoryServiceGrpc.getGenerateTrajectoryMethod = getGenerateTrajectoryMethod =
              io.grpc.MethodDescriptor.<org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest, org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GenerateTrajectory"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new VehicleTrajectoryServiceMethodDescriptorSupplier("GenerateTrajectory"))
              .build();
        }
      }
    }
    return getGenerateTrajectoryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static VehicleTrajectoryServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VehicleTrajectoryServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VehicleTrajectoryServiceStub>() {
        @java.lang.Override
        public VehicleTrajectoryServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VehicleTrajectoryServiceStub(channel, callOptions);
        }
      };
    return VehicleTrajectoryServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static VehicleTrajectoryServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VehicleTrajectoryServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VehicleTrajectoryServiceBlockingStub>() {
        @java.lang.Override
        public VehicleTrajectoryServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VehicleTrajectoryServiceBlockingStub(channel, callOptions);
        }
      };
    return VehicleTrajectoryServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static VehicleTrajectoryServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VehicleTrajectoryServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VehicleTrajectoryServiceFutureStub>() {
        @java.lang.Override
        public VehicleTrajectoryServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VehicleTrajectoryServiceFutureStub(channel, callOptions);
        }
      };
    return VehicleTrajectoryServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void generateTrajectory(org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest request,
        io.grpc.stub.StreamObserver<org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenerateTrajectoryMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service VehicleTrajectoryService.
   */
  public static abstract class VehicleTrajectoryServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return VehicleTrajectoryServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service VehicleTrajectoryService.
   */
  public static final class VehicleTrajectoryServiceStub
      extends io.grpc.stub.AbstractAsyncStub<VehicleTrajectoryServiceStub> {
    private VehicleTrajectoryServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VehicleTrajectoryServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VehicleTrajectoryServiceStub(channel, callOptions);
    }

    /**
     */
    public void generateTrajectory(org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest request,
        io.grpc.stub.StreamObserver<org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenerateTrajectoryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service VehicleTrajectoryService.
   */
  public static final class VehicleTrajectoryServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<VehicleTrajectoryServiceBlockingStub> {
    private VehicleTrajectoryServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VehicleTrajectoryServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VehicleTrajectoryServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse generateTrajectory(org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenerateTrajectoryMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service VehicleTrajectoryService.
   */
  public static final class VehicleTrajectoryServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<VehicleTrajectoryServiceFutureStub> {
    private VehicleTrajectoryServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VehicleTrajectoryServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VehicleTrajectoryServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse> generateTrajectory(
        org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenerateTrajectoryMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GENERATE_TRAJECTORY = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GENERATE_TRAJECTORY:
          serviceImpl.generateTrajectory((org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest) request,
              (io.grpc.stub.StreamObserver<org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGenerateTrajectoryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.PathRequest,
              org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.TrajectoryResponse>(
                service, METHODID_GENERATE_TRAJECTORY)))
        .build();
  }

  private static abstract class VehicleTrajectoryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    VehicleTrajectoryServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.littletonrobotics.vehicletrajectoryservice.VehicleTrajectoryServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("VehicleTrajectoryService");
    }
  }

  private static final class VehicleTrajectoryServiceFileDescriptorSupplier
      extends VehicleTrajectoryServiceBaseDescriptorSupplier {
    VehicleTrajectoryServiceFileDescriptorSupplier() {}
  }

  private static final class VehicleTrajectoryServiceMethodDescriptorSupplier
      extends VehicleTrajectoryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    VehicleTrajectoryServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (VehicleTrajectoryServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new VehicleTrajectoryServiceFileDescriptorSupplier())
              .addMethod(getGenerateTrajectoryMethod())
              .build();
        }
      }
    }
    return result;
  }
}
