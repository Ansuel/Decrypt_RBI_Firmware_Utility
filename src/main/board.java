package main;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class board {
    
    private final String mnemonic;
    private String friendly, osck, osik;

    private board(String mnemonic) {
        this.mnemonic = mnemonic;
    }
    
    public String getMnemonic() {
        return this.mnemonic;
    }

    public String getFriendly() {
        return this.friendly;
    }

    private board setFriendly(String friendly) {
        this.friendly = friendly;
        return this;
    }

    public String getOsck() {
        return this.osck;
    }

    private board setOsck(String osck) {
        this.osck = osck;
        return this;
    }

    public String getOsik() {
        return this.osik;
    }

    private board setOsik(String osik) {
        this.osik = osik;
        return this;
    }

    @Override
    public int hashCode() {
        return this.mnemonic.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final board other = (board) obj;
        if (!Objects.equals(this.mnemonic, other.mnemonic)) {
            return false;
        }
        if (!Objects.equals(this.osck, other.osck)) {
            return false;
        }
        return Objects.equals(this.osik, other.osik);
    }

    private static final Set<board> BOARD_SET  = new LinkedHashSet<board>();
    static {

        BOARD_SET.add(new board("VANT-6").setFriendly("TG789vac v2")
            .setOsck("546259AFD4E85AA6FFCE358CE0A93452E25A848138A67C142E42FEC79F4F3784")
            //.setOsik("")
        );
        
        BOARD_SET.add(new board("VANT-F").setFriendly("TG799vac")
            .setOsck("7FA2FDF4D4DC31BF66F91DDA9A3E8777B7D7D2EC6E8DB1926C0831CA2A279FDB")
        );
        
        BOARD_SET.add(new board("VANT-Y").setFriendly("TG800vac")
            .setOsck("8E07111F188641948E84506DB65270BD26595AD41327235A53998DB068DC3833")
        );
  
        BOARD_SET.add(new board("VBNT-F").setFriendly("TG789vac Xtream")
            .setOsck("FCD9BE1D6D8EA65968E77A89B8AFCA98A1467FEEE87A87BD276C91DD94D41D59")
        );
        
        BOARD_SET.add(new board("VBNT-H").setFriendly("TG799vac Xtream")
            .setOsck("7EA0FFCCE7B079AC08792A7C7899AEC90D013BA2574A41465502E62B9EBD5588")
        );
        
        BOARD_SET.add(new board("VBNT-J").setFriendly("DJN2130")
            .setOsck("222C4DC4A9DF952B02D5A489A112CF5E29AAEDF86ADB634410D6721F15F451E4")
        );
  
        BOARD_SET.add(new board("VBNT-K").setFriendly("DGA4130")
            .setOsck("FFD56A4E3A21401BF1798B3CD8AD54D238BA80039623BBA08B6D50B8EC73F7B4")
        );

        BOARD_SET.add(new board("VBNT-L").setFriendly("TG789vac v2 HP")
            .setOsck("A484245CCFBE2541B0C5C5E923BE67A7DEB9A823DD5CBAB92CC619DEA1391A42")
        );
        
        BOARD_SET.add(new board("VBNT-O").setFriendly("DGA4131")
            .setOsck("916AEB569D8CBF8CFAF060AEC533D43A9EF0ACB3138F8351C4112674212975A5")
        );
        
        BOARD_SET.add(new board("VBNT-S").setFriendly("DGA4132")
            .setOsck("0EF34D972945869EF40F89873FED30269020E107685C097751BEF9479D75D620")
        );

        BOARD_SET.add(new board("VBNT-V").setFriendly("DJA0230")
            .setOsck("7BFFB7EBBE416D38078712EC5AC5DEF6E4E50EE58848D6F2C072DF6E0C6CEFE7")
        );

        BOARD_SET.add(new board("VBNT-Z").setFriendly("TG789Bvac")
            .setOsck("BA6B79CAACF7A740ACF366AB11DAAA3E48252BD97205AC6D07C558CDDA5ED7CC")
        );

        BOARD_SET.add(new board("VDNT-O").setFriendly("TG799vn v2")
            .setOsck("EFA9268D1455DF20E8F73084E5D67F3D3B91961680E54732178BD7EC5D94AAC3")
        );

        BOARD_SET.add(new board("VDNT-W").setFriendly("TG788vn v2")
            .setOsck("AA7220BC88329ACF74A042B4D45B07B165615B10916CF40B0BCC8608BE9E1D60")
            //.setOsik("")
        );

    }

    private static final Map<String, board> BOARD_MAP = new HashMap<String, board>();
    static {
        BOARD_SET.forEach(board -> {
            BOARD_MAP.put(board.getMnemonic(), board);
        });
    }

    public static Map<String, board> getMap() {
        return new HashMap<>(BOARD_MAP);
    }

    public static board getByMnemonic(String mnemonic) {
        return BOARD_MAP.get(mnemonic);
    }
}
