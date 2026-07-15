package com.webutils.testapp.app;

import org.springframework.stereotype.Service;

import com.webutils.services.common.IWebutilsService;

/**
 * Minimal application callback required by WebUtils ({@link UserService}, {@link SecurityService}).
 * Default methods on the interface are sufficient for the widget harness.
 */
@Service
public class TestAppWebutilsService implements IWebutilsService
{
}
