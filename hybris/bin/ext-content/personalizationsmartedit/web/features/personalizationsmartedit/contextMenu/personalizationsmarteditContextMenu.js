angular.module('personalizationsmarteditContextMenu', ['gatewayProxyModule'])

.factory('personalizationsmarteditContextModal', function(gatewayProxy) {

    var PersonalizationsmarteditContextModal = function() {
        this.gatewayId = "personalizationsmarteditContextModal";
        gatewayProxy.initForService(this);
    };

    PersonalizationsmarteditContextModal.prototype.openDeleteAction = function(componentType, componentId, containerId, slotId, actionId) {};

    PersonalizationsmarteditContextModal.prototype.openAddAction = function(componentType, componentId, containerId, slotId, actionId) {};

    PersonalizationsmarteditContextModal.prototype.openEditAction = function(componentType, componentId, containerId, slotId, actionId) {};

    PersonalizationsmarteditContextModal.prototype.openInfoAction = function() {};

    PersonalizationsmarteditContextModal.prototype.openEditComponentAction = function(componentType, componentId) {};

    return new PersonalizationsmarteditContextModal();
});
