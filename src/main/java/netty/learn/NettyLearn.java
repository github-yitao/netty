package netty.learn;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;

public class NettyLearn {
    public static void main(String[] args)  throws Exception{
        start();
    }

    private static void start() throws Exception{
        final NettyConfig serverHandler = new NettyConfig();
        // ����EventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // ����EventLoopGroup
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                //ָ����ʹ�õ�NIO����Channel
                .channel(NioServerSocketChannel.class)
                //ʹ��ָ���Ķ˿������׽��ֵ�ַ
                .localAddress(new InetSocketAddress(8080))
                // ���һ��EchoServerHandler��Channle��ChannelPipeline
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //EchoServerHandler����עΪ@shareable,�������ǿ�������ʹ��ͬ���İ���
                        socketChannel.pipeline()
                                .addLast(new HttpRequestDecoder())
                                .addLast(new HttpResponseEncoder())
                                .addLast(new HttpObjectAggregator(512 * 1024))
                                .addLast(serverHandler)
                        ;
                    };
                });

        try {
            // �첽�ذ󶨷�����;����sync���������ȴ�ֱ�������
            ChannelFuture f = b.bind().sync();
            // ��ȡChannel��CloseFuture������������ǰ�߳�ֱ�������
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // ���ŵĹر�EventLoopGroup���ͷ����е���Դ
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
