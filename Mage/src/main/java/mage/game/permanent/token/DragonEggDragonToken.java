package mage.game.permanent.token;

import mage.MageInt;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.continuous.BoostSourceEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;

/**
 * @author spjspj
 */
public final class DragonEggDragonToken extends TokenImpl {

    public DragonEggDragonToken() {
        super("Dragon Token", "2/2 red Dragon creature token with flying and \"{R}: This token gets +1/+0 until end of turn.\"");
        cardType.add(CardType.CREATURE);
        color.setRed(true);
        subtype.add(SubType.DRAGON);
        power = new MageInt(2);
        toughness = new MageInt(2);

        this.addAbility(FlyingAbility.getInstance());
        this.addAbility(new SimpleActivatedAbility(new BoostSourceEffect(1, 0, Duration.EndOfTurn), new ManaCostsImpl<>("{R}")));
    }

    private DragonEggDragonToken(final DragonEggDragonToken token) {
        super(token);
    }

    public DragonEggDragonToken copy() {
        return new DragonEggDragonToken(this);
    }
}
