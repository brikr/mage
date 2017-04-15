/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.e;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.condition.Condition;
import mage.abilities.condition.common.SourceHasCounterCondition;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.decorator.ConditionalActivatedAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.RestrictionEffect;
import mage.abilities.effects.common.combat.CantAttackTargetEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.turn.Step;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author jeffwadsworth
 */
public class EdificeOfAuthority extends CardImpl {

    private static final String rule = "{1}, {T}: Until your next turn, target creature can't attack or block and its activated abilities can't be activated. Activate this ability only if there are three or more brick counter on {this}.";

    public EdificeOfAuthority(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{3}");

        // {1}, {T}: Target creature can't attack this turn. Put a brick counter on Edifice of Authority.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new CantAttackTargetEffect(Duration.EndOfTurn), new ManaCostsImpl("{1}"));
        ability.addCost(new TapSourceCost());
        ability.addEffect(new AddCountersSourceEffect(CounterType.BRICK.createInstance()));
        ability.addTarget(new TargetCreaturePermanent());
        this.addAbility(ability);

        // {1}, {T}: Until your next turn, target creature can't attack or block and its activated abilities can't be activated. Activate this ability only if there are three or more brick counter on Edifice of Authority.
        Condition condition = new SourceHasCounterCondition(CounterType.BRICK, 3);
        Ability ability2 = new ConditionalActivatedAbility(Zone.BATTLEFIELD, new EdificeOfAuthorityEffect(), new ManaCostsImpl("{1}"), condition, rule);
        ability2.addCost(new TapSourceCost());
        ability2.addTarget(new TargetCreaturePermanent());
        this.addAbility(ability2);

    }

    public EdificeOfAuthority(final EdificeOfAuthority card) {
        super(card);
    }

    @Override
    public EdificeOfAuthority copy() {
        return new EdificeOfAuthority(this);
    }
}

class EdificeOfAuthorityEffect extends OneShotEffect {

    public EdificeOfAuthorityEffect() {
        super(Outcome.LoseAbility);
    }

    public EdificeOfAuthorityEffect(String ruleText) {
        super(Outcome.LoseAbility);
        staticText = ruleText;
    }

    public EdificeOfAuthorityEffect(final EdificeOfAuthorityEffect effect) {
        super(effect);
    }

    @Override
    public EdificeOfAuthorityEffect copy() {
        return new EdificeOfAuthorityEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        EdificeOfAuthorityRestrictionEffect effect = new EdificeOfAuthorityRestrictionEffect();
        game.addEffect(effect, source);
        return true;
    }
}

class EdificeOfAuthorityRestrictionEffect extends RestrictionEffect {

    public EdificeOfAuthorityRestrictionEffect() {
        super(Duration.Custom);
        staticText = "";
    }

    public EdificeOfAuthorityRestrictionEffect(final EdificeOfAuthorityRestrictionEffect effect) {
        super(effect);
    }

    @Override
    public void init(Ability source, Game game) {
        super.init(source, game);
        for (UUID targetId : this.getTargetPointer().getTargets(game, source)) {
            Permanent permanent = game.getPermanent(targetId);
            if (permanent != null) {
                permanent.addInfo("Can't attack or block and its activated abilities can't be activated." + getId(), "", game);
            }
        }
    }

    @Override
    public boolean isInactive(Ability source, Game game) {
        if (game.getPhase().getStep().getType() == PhaseStep.UNTAP
                && game.getStep().getStepPart() == Step.StepPart.PRE) {
            if (game.getActivePlayerId().equals(source.getControllerId())
                    || game.getPlayer(source.getControllerId()).hasReachedNextTurnAfterLeaving()) {
                for (UUID targetId : this.getTargetPointer().getTargets(game, source)) {
                    Permanent permanent = game.getPermanent(targetId);
                    if (permanent != null) {
                        permanent.addInfo("Can't attack or block and its activated abilities can't be activated." + getId(), "", game);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean applies(Permanent permanent, Ability source, Game game) {
        if (this.targetPointer.getTargets(game, source).contains(permanent.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canAttack(Game game) {
        return false;
    }

    @Override
    public boolean canBlock(Permanent attacker, Permanent blocker, Ability source, Game game) {
        return false;
    }

    @Override
    public boolean canUseActivatedAbilities(Permanent permanent, Ability source, Game game) {
        return false;
    }

    @Override
    public EdificeOfAuthorityRestrictionEffect copy() {
        return new EdificeOfAuthorityRestrictionEffect(this);
    }

}