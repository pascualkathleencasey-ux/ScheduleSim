package gui;

import cpu.RoundRobinScheduler;
import cpu.SRTFScheduler;
import disk.CLookScheduler;
import disk.ScanScheduler;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * The single window of the program. A CardLayout holds one screen per card:
 * the menu, the four algorithm screens, and About. Screens switch cards
 * through the Navigator, so they never reference each other.
 */
public class MainFrame extends JFrame implements Navigator {
    private static final long serialVersionUID = 1L;

    static final String CARD_MENU = "menu";
    static final String CARD_SRTF = "srtf";
    static final String CARD_RR = "rr";
    static final String CARD_CLOOK = "clook";
    static final String CARD_SCAN = "scan";
    static final String CARD_ABOUT = "about";

    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);

    public MainFrame() {
        super(AppInfo.TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        container.add(new MenuPanel(this), CARD_MENU);
        container.add(new CpuPanel(new SRTFScheduler(), this), CARD_SRTF);
        container.add(new CpuPanel(new RoundRobinScheduler(), this), CARD_RR);
        container.add(new DiskPanel(new CLookScheduler(), this), CARD_CLOOK);
        container.add(new DiskPanel(new ScanScheduler(), this), CARD_SCAN);
        container.add(new AboutPanel(this), CARD_ABOUT);

        setContentPane(container);
        setMinimumSize(new Dimension(760, 560));
        setSize(980, 760);
        setLocationRelativeTo(null);
    }

    @Override
    public void show(String card) {
        cards.show(container, card);
    }
}
