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

package ome.services.blitz.impl;

import ome.services.blitz.util.BlitzExecutor;
import omero.ServerError;

import omero.api.AMD_IReceptorLightService_getPlasmid;

import ome.api.IReceptorLightService;
import omero.api._IReceptorLightServiceOperations;
import Ice.Current;

public class ReceptorLightServiceI extends AbstractAmdServant implements _IReceptorLightServiceOperations
{
    public ReceptorLightServiceI(IReceptorLightService service, BlitzExecutor be)
    {
        super(service, be);
    }

    public void getPlasmid_async(AMD_IReceptorLightService_getPlasmid __cb,
            long id, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current, id);
    }
}

