/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.intellij.kubernetes.model

import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.client.NamespacedKubernetesClient

class NamespaceProvider(private val client: NamespacedKubernetesClient, val namespace: HasMetadata) {

    private val kindProviders: List<ResourceKindProvider> = mutableListOf(PodsProvider(client, namespace))

    fun getName(): String {
        return namespace.metadata.name
    }

    fun getPods(): List<Pod> {
        return getResources(PodsProvider.KIND)
    }

    fun hasResource(resource: HasMetadata): Boolean {
        if (namespace == resource) {
            return true
        }
        return kindProviders.stream()
            .anyMatch{ it.hasResource(resource) }
    }

    fun clear(resource: HasMetadata) {
        kindProviders.find { it.hasResource(resource) }
            ?.clear(resource)
    }

    fun clear(kind: Class<HasMetadata>) {
        kindProviders.find { kind == it.kind }
            ?.clear()
    }

    fun clear() {
        kindProviders.forEach{ it.clear() }
    }

    private fun <T> getResources(kind: Class<T>): List<T> {
        val provider: ResourceKindProvider? = kindProviders.find { kind == it.kind }
        return (provider?.allResources as? List<T>) ?: emptyList()
    }
}
