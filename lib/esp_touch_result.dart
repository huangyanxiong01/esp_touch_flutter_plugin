class EspTouchResult {
  String address;
  String bssid;

  EspTouchResult(this.address, this.bssid);

  Map<String, String> toJson() {
    return {"address": address, "bssid": bssid};
  }

  EspTouchResult.fromJson(Map<String, dynamic> map)
      : address = map["address"],
        bssid = map["bssid"];
}
