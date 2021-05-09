/** *****************************************************************************
 * Copyright (C) 2019, Christian Marangi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***************************************************************************** */
package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class board {

    private final String mnemonic;
    private String friendly;
    private final ObservableList<os_key_couple> osKeys = FXCollections.observableArrayList();

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

    public ObservableList<os_key_couple> getAllOsKeys() {
        return this.osKeys;
    }

    private board addOsKeys(String osck, String osik) {
        this.osKeys.add(new os_key_couple(osik, osck));
        return this;
    }

    private board addOsKeys(String osck, Osik osik) {
        return addOsKeys(osck, osik.keyvalue);
    }

    private board addOsKeys(String osck) {
        return addOsKeys(osck, Osik.Technicolor);
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
        return Objects.equals(this.mnemonic, other.mnemonic);
    }

    private enum Osik {
        Technicolor("D9A2AECBEA13AD7BFF2B6B9CCED220FDC64A6FA3444B54CE4539F7E83998EEF2537C9B006C290C027074211AF9D957983B7E5A580A608EB1C1D9FBAD620F068E4DAA9E1BC41101A645C443E890D3C8B4C61899E56571286056BA2742B8F52A0440CC95A94EA6F047555B66C833C20EAD7322C6B422E289D625194C84A542A02A41F715D326C83163F11D28058086BBFD6DF657A0A89B51FD4D20425A1587C9965A1EC0AD062ACDA3E2E47F973C05E67A32D5EC20E5E33B54994E157EF5D1BDFC59887216AE92FEF4449C781E32C03B739AF81C2DC590603FA9A52611631CA6F7BE71BA8CC9F7D8F6A09B4E162E0694BD964A4405C5DFF04454931AE563757CB5"),
        Telia("C4F0DE2D6C5C2A9F031D6B3BAE4E698B13F38939E8FB2E0DEB70FFEA84219EA37F986790DE82C8A3937F66859F5F9B7657BC9146F123E5A03AE2CA7EDCAD4A7DFBA15ACA0FE6F9A7800D77AA66E87999E0EE7ADDAB4F30AACD3B2D2A4B3C260A9D9950FA10B6C896821F17120FE7D4BEF0482EA1FCAEA2DE12BF765DA5C1B19A51CC8F514C17A7ED7163027B8AE88B50D4F731D3A266DC541B8DC4AFF8E26551C2B2559D6BF884CCB1E82279775B82CC2A725697E7627C484887DA5E371D5CFCE8CF05545732D05FE383EEE0BBE5881AA0A182B49DECBBB36253EB76E9C6B7F922D8246E3159C41535B083EB3A16FA442158CA8189578F3EF56492A163E7E07F");

        public final String keyvalue;

        private Osik(String keyvalue) {
            this.keyvalue = keyvalue;
        }
    }

    private static final Set<board> BOARD_SET = new LinkedHashSet<board>();

    static {

        BOARD_SET.add(new board("DANT-7").setFriendly("TG582n v2")
                .addOsKeys("A568CBE57060A8F6E5ECA4E5C8CB7CEB09FCE0A0D12020F4B258404B04927053")
        );

        BOARD_SET.add(new board("GANT-1").setFriendly("TG389ac")
                .addOsKeys("0373853314B486E335E464B3872E56396634DFD5B6DB9BD03A3DFADE0E37146D")
        );

        BOARD_SET.add(new board("GBNT-2").setFriendly("FGA2130")
                .addOsKeys("F1C7F709C3D894302C2F8454553E448DF42ADC99981E593BBF4DB3B87CAF9F5B")
        );

        BOARD_SET.add(new board("VANT-2").setFriendly("TG588v v2")
                .addOsKeys("B0E88249B150C934AC2BA35E7A7C712B3244833ED8DA10ABAB5DAA91AE9F455F")
        );

        BOARD_SET.add(new board("VANT-6").setFriendly("TG789vac v2")
                .addOsKeys("546259AFD4E85AA6FFCE358CE0A93452E25A848138A67C142E42FEC79F4F3784")
        );

        BOARD_SET.add(new board("VANT-9").setFriendly("DGA0130")
                .addOsKeys("89BCC09EABE21FA738E62E6D911FA80CAF091233ECCFF88442FAA5D7AF651A30")
        );

        BOARD_SET.add(new board("VANT-F").setFriendly("TG799vac")
                .addOsKeys("7FA2FDF4D4DC31BF66F91DDA9A3E8777B7D7D2EC6E8DB1926C0831CA2A279FDB")
        );

        BOARD_SET.add(new board("VANT-R").setFriendly("TG799vac")
                .addOsKeys("395A2C3E2618CF477AA9AD686FB001F86B06FC3475FA7F283589017D70DBA0BE", Osik.Telia)
        );

        BOARD_SET.add(new board("VANT-W").setFriendly("TG799vac Xtream")
                .addOsKeys("B220346F6CCC903545A1FDB458AAE8248A4BF7786031D4D0A33ECB31BB237FD0")
                .addOsKeys("4463C4C33FE788B3CD2BD408706BDF07799D4873A7D50D65DB205CF65E34F87E", Osik.Telia)
        );

        BOARD_SET.add(new board("VANT-Y").setFriendly("TG800vac")
                .addOsKeys("8E07111F188641948E84506DB65270BD26595AD41327235A53998DB068DC3833")
        );

        BOARD_SET.add(new board("VBNT-1").setFriendly("TG789vac v3")
                .addOsKeys("C61669DB317E14BB28F180E8B2B2F78ED4F654DE4D2E53069C87B55CED840A16")
        );

        BOARD_SET.add(new board("VBNT-F").setFriendly("TG789vac Xtream")
                .addOsKeys("FCD9BE1D6D8EA65968E77A89B8AFCA98A1467FEEE87A87BD276C91DD94D41D59")
        );

        BOARD_SET.add(new board("VBNT-H").setFriendly("TG799vac Xtream")
                .addOsKeys("7EA0FFCCE7B079AC08792A7C7899AEC90D013BA2574A41465502E62B9EBD5588", Osik.Telia)
        );

        BOARD_SET.add(new board("VBNT-J").setFriendly("DJN2130")
                .addOsKeys("222C4DC4A9DF952B02D5A489A112CF5E29AAEDF86ADB634410D6721F15F451E4")
        );

        BOARD_SET.add(new board("VBNT-K").setFriendly("DGA4130")
                .addOsKeys("FFD56A4E3A21401BF1798B3CD8AD54D238BA80039623BBA08B6D50B8EC73F7B4")
        );

        BOARD_SET.add(new board("VBNT-L").setFriendly("TG789vac v2 HP")
                .addOsKeys("A484245CCFBE2541B0C5C5E923BE67A7DEB9A823DD5CBAB92CC619DEA1391A42")
        );

        BOARD_SET.add(new board("VBNT-O").setFriendly("DGA4131")
                .addOsKeys("916AEB569D8CBF8CFAF060AEC533D43A9EF0ACB3138F8351C4112674212975A5")
        );

        BOARD_SET.add(new board("VBNT-S").setFriendly("DGA4132")
                .addOsKeys("0EF34D972945869EF40F89873FED30269020E107685C097751BEF9479D75D620")
        );

        BOARD_SET.add(new board("VBNT-V").setFriendly("DJA0230")
                .addOsKeys("7BFFB7EBBE416D38078712EC5AC5DEF6E4E50EE58848D6F2C072DF6E0C6CEFE7")
        );

        BOARD_SET.add(new board("VBNT-Z").setFriendly("DNA0130")
                .addOsKeys("BA6B79CAACF7A740ACF366AB11DAAA3E48252BD97205AC6D07C558CDDA5ED7CC")
        );

        BOARD_SET.add(new board("VCNT-A").setFriendly("DJA0231")
                .addOsKeys("AD8A87E29BE6B6ED72F575C1C80BBD638E7CE95E5BF9824145FD7D042CDA2D79")
        );

        BOARD_SET.add(new board("VCNT-3").setFriendly("DGA4331")
                .addOsKeys("AFE6A928EFD1D5824CCA03CF6F9CD7796FCBE9A746AA7120792B666FC2C3EBB6")
        );

        BOARD_SET.add(new board("VDNT-O").setFriendly("TG799vn v2")
                .addOsKeys("EFA9268D1455DF20E8F73084E5D67F3D3B91961680E54732178BD7EC5D94AAC3")
                .addOsKeys("7CDC61993A2FAEF64033705515AFE8B152FB4B1AF0B129F7E91C63C5D3FEB699", Osik.Telia)
        );

        BOARD_SET.add(new board("VDNT-W").setFriendly("TG788vn v2")
                .addOsKeys("AA7220BC88329ACF74A042B4D45B07B165615B10916CF40B0BCC8608BE9E1D60")
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
