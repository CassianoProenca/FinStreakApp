package com.financial.app.events;

import java.util.UUID;

public record CheckInEvent(UUID userId, String note) {}