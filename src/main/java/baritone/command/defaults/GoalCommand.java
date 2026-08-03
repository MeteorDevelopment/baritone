/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.RelativeCoordinate;
import baritone.api.command.datatypes.RelativeGoal;
import baritone.api.command.exception.CommandException;
import baritone.api.command.helpers.TabCompleteHelper;
import baritone.api.pathing.goals.Goal;
import baritone.api.process.ICustomGoalProcess;
import baritone.api.utils.BetterBlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GoalCommand extends Command {

    public GoalCommand(IBaritone baritone) {
        super(baritone, "goal");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        ICustomGoalProcess goalProcess = baritone.getCustomGoalProcess();
        if (args.hasAny() && Arrays.asList("reset", "clear", "none").contains(args.peekString())) {
            args.requireMax(1);
            if (goalProcess.getGoal() != null) {
                goalProcess.setGoal(null);
                logDirect(tr("command.goal.cleared"));
            } else {
                logDirect(tr("command.goal.noneToClear"));
            }
        } else {
            args.requireMax(3);
            BetterBlockPos origin = ctx.playerFeet();
            Goal goal = args.getDatatypePost(RelativeGoal.INSTANCE, origin);
            goalProcess.setGoal(goal);
            logDirect(tr("command.goal.set", goal.toString()));
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        TabCompleteHelper helper = new TabCompleteHelper();
        if (args.hasExactlyOne()) {
            helper.append("reset", "clear", "none", "~");
        } else {
            if (args.hasAtMost(3)) {
                while (args.has(2)) {
                    if (args.peekDatatypeOrNull(RelativeCoordinate.INSTANCE) == null) {
                        break;
                    }
                    args.get();
                    if (!args.has(2)) {
                        helper.append("~");
                    }
                }
            }
        }
        return helper.filterPrefix(args.getString()).stream();
    }

    @Override
    public String getShortDesc() {
        return tr("command.goal.shortDesc");
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(tr("command.goal.longDesc").split("\n"));
    }
}
