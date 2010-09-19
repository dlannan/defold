#include "res_fragment_program.h"

#include <graphics/graphics_device.h>

namespace dmGameSystem
{
    dmResource::CreateResult ResFragmentProgramCreate(dmResource::HFactory factory,
                                                   void* context,
                                                   const void* buffer, uint32_t buffer_size,
                                                   dmResource::SResourceDescriptor* resource,
                                                   const char* filename)
    {
        dmGraphics::HFragmentProgram prog = dmGraphics::CreateFragmentProgram(buffer, buffer_size);
        if (prog == 0 )
            return dmResource::CREATE_RESULT_UNKNOWN;

        resource->m_Resource = (void*) prog;
        return dmResource::CREATE_RESULT_OK;
    }

    dmResource::CreateResult ResFragmentProgramDestroy(dmResource::HFactory factory,
                                                    void* context,
                                                    dmResource::SResourceDescriptor* resource)
    {
        dmGraphics::DestroyFragmentProgram((dmGraphics::HFragmentProgram) resource->m_Resource);
        return dmResource::CREATE_RESULT_OK;
    }
}
