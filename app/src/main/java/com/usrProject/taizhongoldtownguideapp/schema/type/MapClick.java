package com.usrProject.taizhongoldtownguideapp.schema.type;


import com.usrProject.taizhongoldtownguideapp.R;

public enum MapClick {
    G(R.string.大屯郡守官舍,655.0,1725.0,755.0,1780.0),
    H(R.string.大屯郡役所,1530.0,1500.0,1665.0,1565.0),
    I(R.string.大同國小前棟大樓,1755.0,1870.0,1900.0,1940.0),
    E(R.string.中山綠橋,2728.0,1582.0,2847.0,1636.0),
    J(R.string.中區第一任街長宅邸,2110.0,930.0,2200.0,1000.0),
//    K(R.string.元保宮廟,0.0,0.0,0.0,0.0),
    L(R.string.公賣局第五酒廠,2220.0,2200.0,2650.0,2400.0),
    M(R.string.文英館,3415.0,420.0,3530.0,500.0),
    N(R.string.文學館,640.0,1350.0,850.0,1430.0),
    O(R.string.水源地上水塔, 3650.0,150.0,3760.0,500.0),
    P(R.string.臺中火車站附屬設施建築群27號倉庫,2880.0,1850.0,2975.0,1900.0),
    Q(R.string.臺中車站周圍防空壕及碉堡群,3180.0,1820.0,3300.0,1867.0),
    S(R.string.台中火車站, 3120.0,1665.0,3370.0,1770.0),
    T(R.string.台中市市長公館,3320.0,320.0,3440.0,400.0),
    U(R.string.台中市役所,1980.0,1530.0,2100.0,1620.0),
    V(R.string.台中市警察局第一分局,1300.0,1525.0,1400.0,1600.0),
    F(R.string.臺中市後火車站,3295.0,1804.0,3418.0,1840.0),
    W(R.string.台中放送局,3830.0,200.0,4000.0,345.0),
    X(R.string.台中教育大學前棟大樓,670.0,980.0,830.0,1050.0),
    Y(R.string.台中第四市場,4623.0,1195.0,4722.0,1258.0),
    Z(R.string.台灣省成大北門,3017.0,690.0,3087.0,755.0),
    A(R.string.四維街日式招待所,1435.0,1749.0,1560.0,1815.0),
    AA(R.string.民生路56巷日式宿舍,1445.0,1451.0,1520.0,1500.0),
    AB(R.string.刑務所典獄長官舍,1095.0,2160.0,1174.0,2212.0),
    AC(R.string.刑務所浴場, 1035.0,2127.0,1080.0,2169.0),
    C(R.string.合作金庫銀行臺中分行,2230.0,1560.0,2369.0,1621.0),
    AD(R.string.地方法院舊宿舍群,1060.0,1930.0,1150.0,1986.0),
    AE(R.string.林之助紀念館,1115.0,1130.0,1220.0,1173.0),
    AF(R.string.林森路75號日式宿舍,590.0,1777.0,660.0,1821.0),
    AG(R.string.帝國製糖廠臺中營業所,4355.0,1531.0,4601.0,1724.0),
    AH(R.string.柳原教會,2328.0,616.0,2400.0,674.0),
    AI(R.string.孫立人將軍故居,0.0,505.0,95.0,561.0),
//    AJ(R.string.崇倫碉堡,0.0,0.0,0.0,0.0),
//    AK(R.string.莒光火車,2400.0,1900.0,2580.0,1940.0),
    AL(R.string.湖心亭_中正橋,2930.0,860.0,3140.0,965.0),
    AV(R.string.順天宮將軍廟,1500.0,800.0,1600.0,875.0),
    AM(R.string.新民街8_10號倉庫,4050.0,1400.0,4180.0,1447.0),
//    AN(R.string.萬和宮,0.0,0.0,0.0,0.0),
    AO(R.string.萬春宮,2487.0,1060.0,2565.0,1111.0),
    AP(R.string.農委會農糧署中區分署臺中辦事處,2470.0,339.0,2581.0,417.0),
    D(R.string.彰化銀行舊總行_株式會社彰化銀行本店,2560.0,1360.0,2683.0,1443.0),
    B(R.string.彰化銀行繼光街宿舍,2120.0,1695.0,2257.0,1748.0),
    AQ(R.string.演武場,950.0,2015.0,1044.0,2114.0),
    AS(R.string.臺中刑務所官舍群,1130.0,2107.0,1249.0,2155.0),
//    AT(R.string.樂成宮,0.0,0.0,0.0,0.0),
    AU(R.string.儒考棚,1538.0,1663.0,1639.0,1720.0);
    public int documentId;
    public Double startX,startY;
    public Double endX,endY;

    MapClick(int documentId, Double startX, Double startY, Double endX, Double endY){
        this.documentId = documentId;

        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

}
