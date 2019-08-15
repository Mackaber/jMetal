package me.mackaber.tesis.Util;

import org.uma.jmetal.util.naming.DescribedEntity;

import java.util.List;

public abstract class Function implements DescribedEntity {
    public abstract double eval(List<User> group);
}
