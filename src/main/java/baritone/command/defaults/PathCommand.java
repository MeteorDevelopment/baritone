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

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandException;
import baritone.api.process.ICustomGoalProcess;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PathCommand extends Command {

    public PathCommand(IBaritone baritone) {
        super(baritone, "path");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        ICustomGoalProcess customGoalProcess = baritone.getCustomGoalProcess();
        args.requireMax(0);
        BaritoneAPI.getProvider().getWorldScanner().repack(ctx);
        customGoalProcess.path();
        logDirect(tr("command.path.now"));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return tr("command.path.shortDesc");
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(tr("command.path.longDesc").split("\n"));
    }
}
