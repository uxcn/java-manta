/*
 * Copyright (c) 2017, Joyent, Inc. All rights reserved.
 */
package com.joyent.manta.client.multipart;

import com.joyent.manta.client.MantaClient;

/**
 * Convenience class that implements the generic interface for an encrypted
 * jobs-based multipart manager and creates the underlying wrapped instance
 * so that the API consumer doesn't have to wrangle with so many generics.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 3.0.0
 */
public class EncryptedJobsMultipartManager
        extends EncryptedMultipartManager<JobsMultipartManager, JobsMultipartUpload> {
    /**
     * Creates a new encrypted multipart upload manager that is backed by
     * a jobs-based manager.
     *
     * @param mantaClient manta client use for MPU operations
     */
    public EncryptedJobsMultipartManager(final MantaClient mantaClient) {
        super(mantaClient, new JobsMultipartManager(mantaClient));
    }
}
