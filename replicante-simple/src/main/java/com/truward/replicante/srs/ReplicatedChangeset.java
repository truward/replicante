package com.truward.replicante.srs;

import com.truward.replicante.api.ReplicatedEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ReplicatedChangeset {
  private final List<ReplicatedEntity> entities;

  public ReplicatedChangeset(Collection<ReplicatedEntity> entities) {
    this.entities = Collections.unmodifiableList(new ArrayList<>(entities));
  }

  public List<ReplicatedEntity> getEntities() {
    return entities;
  }
}
