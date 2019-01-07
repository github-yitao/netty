package netty.learn;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;


@ChannelHandler.Sharable
public class NettyConfig extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("��������");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("�Ͽ�");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try{
            if (msg instanceof FullHttpRequest) {
                @SuppressWarnings("unchecked")
                FullHttpRequest header = (FullHttpRequest) msg;
                System.out.println("header" + header);
                header.method();
                header.headers().get("Host");
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer(header.headers().get("Host").getBytes("UTF-8")));
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                //�����յ�����Ϣд�������ߣ�������ˢ��վ��Ϣ
                ctx.write(response);
            }else if(ctx instanceof HttpRequest){
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                //�����յ�����Ϣд�������ߣ�������ˢ��վ��Ϣ
                ctx.write(response);
            } else if(ctx instanceof HttpResponse){
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer("hello ".getBytes("UTF-8")));
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                //�����յ�����Ϣд�������ߣ�������ˢ��վ��Ϣ
                ctx.write(response);
            }else{
                release = false;
            }
            //���ͻ��˴������Ϣת��ΪNetty��ByteBuf����
           /* if(msg instanceof FullHttpRequest) {
                FullHttpRequest header = (FullHttpRequest) msg;
                System.out.println("header" + header);
                header.method();
                header.headers().get("Host");
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer(header.headers().get("Host").getBytes("UTF-8")));
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                //�����յ�����Ϣд�������ߣ�������ˢ��վ��Ϣ
                ctx.write(response);
            }*//*else if(msg instanceof LastHttpContent){
           LastHttpContent body = (LastHttpContent) msg;
           System.out.println("body="+body);
           DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
           //�����յ�����Ϣд�������ߣ�������ˢ��վ��Ϣ
           ctx.writeAndFlush(response);
        }*/
        }finally {
            if ( release) {
                ReferenceCountUtil.release(msg);
            }
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // ��δ������Ϣ��ˢ��Զ�̽ڵ㣬 ���ҹرո�Channel
       /* ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);*/
       super.channelReadComplete(ctx);
       ctx.flush();
    }
    /**
     * �쳣����
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //��ӡ�쳣ջ����
        cause.printStackTrace();
        System.err.println("243434334433434");
        // �رո�Channel
        ctx.close();
    }

}
