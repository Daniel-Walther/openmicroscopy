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

package ome.logic;

import ome.annotations.RolesAllowed;
import ome.api.ServiceInterface;
import ome.api.IReceptorLightService;
import org.springframework.transaction.annotation.Transactional;

import ome.model.core.Plasmid;

@Transactional(readOnly = true)
public class ReceptorLightServiceImpl extends AbstractLevel1Service implements IReceptorLightService
{	
    public Class<? extends ServiceInterface> getServiceInterface()
    {
        return IReceptorLightService.class;
    }
    
    @RolesAllowed("user")
    @SuppressWarnings("unchecked")
    public Plasmid getPlasmid(long id)
    {
        Plasmid plasmid = new Plasmid();
        plasmid.setPlasmidId((int)id);

        return plasmid;
    }
}
