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
import baritone.api.command.exception.CommandException;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.process.IBaritoneProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Contains the pause, resume, and paused commands.
 * <p>
 * This thing is scoped to hell, private so far you can't even access it using reflection, because you AREN'T SUPPOSED
 * TO USE THIS to pause and resume Baritone. Make your own process that returns {@link PathingCommandType#REQUEST_PAUSE
 * REQUEST_PAUSE} as needed.
 */
public class ExecutionControlCommands {

    Command pauseCommand;
    Command resumeCommand;
    Command pausedCommand;
    Command cancelCommand;

    public ExecutionControlCommands(IBaritone baritone) {
        // array for mutability, non-field so reflection can't touch it
        final boolean[] paused = {false};
        baritone.getPathingControlManager().registerProcess(
                new IBaritoneProcess() {
                    @Override
                    public boolean isActive() {
                        return paused[0];
                    }

                    @Override
                    public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel) {
                        baritone.getInputOverrideHandler().clearAllKeys();
                        return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
                    }

                    @Override
                    public boolean isTemporary() {
                        return true;
                    }

                    @Override
                    public void onLostControl() {
                    }

                    @Override
                    public double priority() {
                        return DEFAULT_PRIORITY + 1;
                    }

                    @Override
                    public String displayName0() {
                        return "Pause/Resume Commands";
                    }
                }
        );
        pauseCommand = new Command(baritone, "pause", "p", "paws") {
            @Override
            public void execute(String label, IArgConsumer args) throws CommandException {
                args.requireMax(0);
                if (paused[0]) {
                    throw new CommandInvalidStateException(tr("command.pause.already"));
                }
                paused[0] = true;
                logDirect(tr("command.pause.ok"));
            }

            @Override
            public Stream<String> tabComplete(String label, IArgConsumer args) {
                return Stream.empty();
            }

            @Override
            public String getShortDesc() {
                return tr("command.pause.shortDesc");
            }

            @Override
            public List<String> getLongDesc() {
                return Arrays.asList(tr("command.pause.longDesc").split("\n"));
            }
        };
        resumeCommand = new Command(baritone, "resume", "r", "unpause", "unpaws") {
            @Override
            public void execute(String label, IArgConsumer args) throws CommandException {
                args.requireMax(0);
                baritone.getBuilderProcess().resume();
                if (!paused[0]) {
                    throw new CommandInvalidStateException(tr("command.resume.notPaused"));
                }
                paused[0] = false;
                logDirect(tr("command.resume.ok"));
            }

            @Override
            public Stream<String> tabComplete(String label, IArgConsumer args) {
                return Stream.empty();
            }

            @Override
            public String getShortDesc() {
                return tr("command.resume.shortDesc");
            }

            @Override
            public List<String> getLongDesc() {
                return Arrays.asList(tr("command.resume.longDesc").split("\n"));
            }
        };
        pausedCommand = new Command(baritone, "paused") {
            @Override
            public void execute(String label, IArgConsumer args) throws CommandException {
                args.requireMax(0);
                logDirect(tr("command.paused.state", paused[0] ? "" : "not "));
            }

            @Override
            public Stream<String> tabComplete(String label, IArgConsumer args) {
                return Stream.empty();
            }

            @Override
            public String getShortDesc() {
                return tr("command.paused.shortDesc");
            }

            @Override
            public List<String> getLongDesc() {
                return Arrays.asList(tr("command.paused.longDesc").split("\n"));
            }
        };
        cancelCommand = new Command(baritone, "cancel", "c", "stop") {
            @Override
            public void execute(String label, IArgConsumer args) throws CommandException {
                args.requireMax(0);
                if (paused[0]) {
                    paused[0] = false;
                }
                baritone.getPathingBehavior().cancelEverything();
                logDirect(tr("command.cancel.ok"));
            }

            @Override
            public Stream<String> tabComplete(String label, IArgConsumer args) {
                return Stream.empty();
            }

            @Override
            public String getShortDesc() {
                return tr("command.cancel.shortDesc");
            }

            @Override
            public List<String> getLongDesc() {
                return Arrays.asList(tr("command.cancel.longDesc").split("\n"));
            }
        };
    }
}
