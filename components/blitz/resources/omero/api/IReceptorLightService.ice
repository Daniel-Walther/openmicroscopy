/*
 *   $Id$
 *
 * Copyright (C) 2016 Friedrich Schiller University Jena.
 * Mathematics and Computer Science Department 
 * All rights reserved.
 *
 * Written by: Daniel Walther, Daniel.Walther(at)uni-jena.de
 *
 */

#ifndef RECEPTOR_LIGHT_SERVICE_ICE
#define RECEPTOR_LIGHT_SERVICE_ICE

#include <omero/ServicesF.ice>

module omero
{
    module api
    {
        ["ami", "amd"] interface IReceptorLightService extends ServiceInterface
        {
            idempotent omero::model::Plasmid getPlasmid(long id) throws ServerError;
        };
    };
};

#endif
