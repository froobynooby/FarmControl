package com.froobworld.farmcontrol.controller.action;

import com.froobworld.farmcontrol.api.action.ActionBuilder;
import org.bukkit.entity.Mob;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class CustomAction extends Action {
    private final Consumer<Mob> doAction;
    private final Consumer<Mob> undoAction;

    private CustomAction(String name, boolean removes, boolean persistent, Consumer<Mob> doAction, Consumer<Mob> undoAction) {
        super(name, removes, persistent);
        this.doAction = doAction;
        this.undoAction = undoAction;
    }

    @Override
    public void doAction(Mob entity) {
        if (doAction != null) {
            doAction.accept(entity);
        }
    }

    @Override
    public void undoAction(Mob entity) {
        if (undoAction != null) {
            undoAction.accept(entity);
        }
    }

    public static class CustomActionBuilder implements com.froobworld.farmcontrol.api.action.ActionBuilder {
        private static final Pattern ACTION_NAME_PATTERN = Pattern.compile("[a-z-]+");
        private final String name;
        private boolean removes = false;
        private boolean persistent = true;
        private Consumer<Mob> doAction;
        private Consumer<Mob> undoAction;

        public CustomActionBuilder(String name) {
            if (!ACTION_NAME_PATTERN.matcher(name).matches()) {
                throw new IllegalArgumentException("Action names may only contain letters and dashes");
            }
            this.name = name;
        }

        @Override
        public ActionBuilder setPersistent(boolean persistent) {
            this.persistent = persistent;
            return this;
        }

        @Override
        public ActionBuilder setRemoves(boolean removes) {
            this.removes = removes;
            return this;
        }

        @Override
        public ActionBuilder onDoAction(Consumer<Mob> consumer) {
            this.doAction = consumer;
            return this;
        }

        @Override
        public ActionBuilder onUndoAction(Consumer<Mob> consumer) {
            this.undoAction = consumer;
            return this;
        }

        @Override
        public com.froobworld.farmcontrol.api.action.Action build() {
            if (doAction == null) {
                throw new IllegalStateException("Must provide a 'do' action");
            }
            if (persistent && undoAction == null && !removes) {
                throw new IllegalStateException("Must provide an 'undo' action if the action is persistent");
            }
            return new CustomAction(name, removes, persistent, doAction, undoAction);
        }
    }

}
