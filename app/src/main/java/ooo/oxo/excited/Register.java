package ooo.oxo.excited;


import me.drakeet.multitype.MultiTypeAdapter;
import ooo.oxo.excited.provider.ChannelEmptyViewProvider;
import ooo.oxo.excited.provider.FooterViewProvider;
import ooo.oxo.excited.provider.HeaderViewProvider;
import ooo.oxo.excited.provider.MusicViewProvider;
import ooo.oxo.excited.provider.PictureViewProvider;
import ooo.oxo.excited.provider.SendingCardHolder;
import ooo.oxo.excited.provider.VideoViewProvider;
import ooo.oxo.excited.provider.WebViewProvider;
import ooo.oxo.excited.provider.data.DataService;
import ooo.oxo.excited.provider.item.EmptyChannel;
import ooo.oxo.excited.provider.item.Footer;
import ooo.oxo.excited.provider.item.Header;
import ooo.oxo.excited.provider.item.Music;
import ooo.oxo.excited.provider.item.Picture;
import ooo.oxo.excited.provider.item.SendingCard;
import ooo.oxo.excited.provider.item.Video;
import ooo.oxo.excited.provider.item.Web;

public final class Register {

    public static void registerItem(MultiTypeAdapter adapter, DataService dataService) {
        registerCommon(adapter, dataService);
    }

    public static void registerChannelItem(MultiTypeAdapter adapter, DataService dataService) {
        adapter.register(Header.class, new HeaderViewProvider());
        registerCommon(adapter, dataService);
    }

    private static void registerCommon(MultiTypeAdapter adapter, DataService dataService) {
        adapter.register(Music.class, new MusicViewProvider(dataService));
        adapter.register(Web.class, new WebViewProvider(dataService));
        adapter.register(Video.class, new VideoViewProvider(dataService));
        adapter.register(Picture.class, new PictureViewProvider(dataService));
        adapter.register(Footer.class, new FooterViewProvider());
        adapter.register(EmptyChannel.class, new ChannelEmptyViewProvider());
        adapter.register(SendingCard.class, new SendingCardHolder());
    }
}
