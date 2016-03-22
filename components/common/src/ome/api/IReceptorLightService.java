/*
 *   $Id$
 *
 *   Copyright (C) 2016 Friedrich Schiller University Jena.
 *   Mathematics and Computer Science Department 
 *   All rights reserved.
 *
 *   Written by: Daniel Walther, Daniel.Walther(at)uni-jena.de
 *
 */

package ome.api;

import ome.model.core.Plasmid;

public interface IReceptorLightService extends ServiceInterface
{
    Plasmid getPlasmid(long id);
}
