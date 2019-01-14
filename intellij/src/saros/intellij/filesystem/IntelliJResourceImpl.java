package saros.intellij.filesystem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import saros.filesystem.IResource;

public abstract class IntelliJResourceImpl implements IResource {

  @Nullable
  @Override
  public Object getAdapter(@NotNull Class<? extends IResource> clazz) {
    return clazz.isInstance(this) ? this : null;
  }
}
