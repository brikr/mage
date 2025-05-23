package mage.cards;

import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.effects.common.ExileAdventureSpellEffect;
import mage.constants.CardType;
import mage.constants.SpellAbilityType;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.game.ExileZone;
import mage.game.Game;
import mage.util.CardUtil;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author phulin
 */
public class AdventureSpellCard extends CardImpl implements SpellOptionCard {

    private AdventureCard adventureCardParent;

    public AdventureSpellCard(UUID ownerId, CardSetInfo setInfo, String adventureName, CardType[] cardTypes, String costs, AdventureCard adventureCardParent) {
        super(ownerId, setInfo, cardTypes, costs, SpellAbilityType.ADVENTURE_SPELL);
        this.subtype.add(SubType.ADVENTURE);

        AdventureCardSpellAbility newSpellAbility = new AdventureCardSpellAbility(getSpellAbility(), adventureName, cardTypes, costs);
        this.replaceSpellAbility(newSpellAbility);
        spellAbility = newSpellAbility;

        this.setName(adventureName);
        this.adventureCardParent = adventureCardParent;
    }

    public void finalizeSpell() {
        if (spellAbility instanceof AdventureCardSpellAbility) {
            ((AdventureCardSpellAbility) spellAbility).finalizeAdventure();
        }
    }

    protected AdventureSpellCard(final AdventureSpellCard card) {
        super(card);
        this.adventureCardParent = card.adventureCardParent;
    }

    @Override
    public UUID getOwnerId() {
        return adventureCardParent.getOwnerId();
    }

    @Override
    public String getExpansionSetCode() {
        return adventureCardParent.getExpansionSetCode();
    }

    @Override
    public String getCardNumber() {
        return adventureCardParent.getCardNumber();
    }

    @Override
    public boolean moveToZone(Zone toZone, Ability source, Game game, boolean flag, List<UUID> appliedEffects) {
        return adventureCardParent.moveToZone(toZone, source, game, flag, appliedEffects);
    }

    @Override
    public boolean moveToExile(UUID exileId, String name, Ability source, Game game, List<UUID> appliedEffects) {
        return adventureCardParent.moveToExile(exileId, name, source, game, appliedEffects);
    }

    @Override
    public AdventureCard getMainCard() {
        return adventureCardParent;
    }

    @Override
    public void setZone(Zone zone, Game game) {
        game.setZone(adventureCardParent.getId(), zone);
        game.setZone(adventureCardParent.getSpellCard().getId(), zone);
    }

    @Override
    public AdventureSpellCard copy() {
        return new AdventureSpellCard(this);
    }

    @Override
    public void setParentCard(CardWithSpellOption card) {
        this.adventureCardParent = (AdventureCard) card;
    }

    @Override
    public AdventureCard getParentCard() {
        return this.adventureCardParent;
    }

    @Override
    public String getIdName() {
        // id must send to main card (popup card hint in game logs)
        return getName() + " [" + adventureCardParent.getId().toString().substring(0, 3) + ']';
    }

    @Override
    public String getSpellType() {
        return "Adventure";
    }
}

class AdventureCardSpellAbility extends SpellAbility {

    private String nameFull;
    private boolean finalized = false;

    public AdventureCardSpellAbility(final SpellAbility baseSpellAbility, String adventureName, CardType[] cardTypes, String costs) {
        super(baseSpellAbility);
        this.setName(cardTypes, adventureName, costs);
        this.setCardName(adventureName);
    }

    // The exile effect needs to be added last.
    public void finalizeAdventure() {
        if (finalized) {
            throw new IllegalStateException("Wrong code usage. "
                    + "Adventure (" + cardName + ") "
                    + "need to call finalizeAdventure() exactly once.");
        }
        this.addEffect(ExileAdventureSpellEffect.getInstance());
        this.finalized = true;
    }

    protected AdventureCardSpellAbility(final AdventureCardSpellAbility ability) {
        super(ability);
        this.nameFull = ability.nameFull;
        if (!ability.finalized) {
            throw new IllegalStateException("Wrong code usage. "
                    + "Adventure (" + cardName + ") "
                    + "need to call finalizeAdventure() at the very end of the card's constructor.");
        }
        this.finalized = true;
    }

    @Override
    public ActivationStatus canActivate(UUID playerId, Game game) {
        ExileZone adventureExileZone = game.getExile().getExileZone(ExileAdventureSpellEffect.adventureExileId(playerId, game));
        Card spellCard = game.getCard(this.getSourceId());
        if (spellCard instanceof AdventureSpellCard) {
            Card card = ((AdventureSpellCard) spellCard).getParentCard();
            if (adventureExileZone != null && adventureExileZone.contains(card.getId())) {
                return ActivationStatus.getFalse();
            }
        }
        return super.canActivate(playerId, game);
    }

    public void setName(CardType[] cardTypes, String name, String costs) {
        this.nameFull = "Adventure " + Arrays.stream(cardTypes).map(CardType::toString).collect(Collectors.joining(" ")) + " &mdash; " + name;
        this.name = this.nameFull + " " + costs;
    }

    @Override
    public String getRule(boolean all) {
        // TODO: must hide rules in permanent like SpellAbility, but can't due effects text
        return this.nameFull
                + " "
                + getManaCosts().getText()
                + " &mdash; "
                + CardUtil.getTextWithFirstCharUpperCase(super.getRule(false)) // without cost
                + " <i>(Then exile this card. You may cast the creature later from exile.)</i>";
    }

    @Override
    public AdventureCardSpellAbility copy() {
        return new AdventureCardSpellAbility(this);
    }
}
